package org.pbl4.pbl4_be.controller;

import org.pbl4.pbl4_be.controller.dto.GameStartDTO;
import org.pbl4.pbl4_be.controller.dto.RoomResponse;
import org.pbl4.pbl4_be.controller.exception.BadRequestException;
import org.pbl4.pbl4_be.controller.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.model.Player;
import org.pbl4.pbl4_be.model.Room;
import org.pbl4.pbl4_be.service.GameRoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final GameRoomManager gameRoomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RoomController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
    }

    // Xử lý yêu cầu tham gia phòng
    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestParam("playerId") String playerId,
                                       @RequestParam("roomCode") String roomCode) {

        System.out.println("Join room" + playerId + " " + roomCode);

        if (gameRoomManager.getRoom(roomCode) == null) {
            throw new BadRequestException("Room not found");
        }

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(playerId);

        // Kiểm tra player đã tham gia phòng khác chưa
        if (roomCodeOfPlayer != null && !Objects.equals(roomCode, roomCodeOfPlayer)) {
            throw new PlayerAlreadyInRoomException("Player is playing in another room");
        }

        // Room này đảm bảo đã tồn tại
        Room room = gameRoomManager.getRoom(roomCode);
        if(!room.checkFull()) {
            room.addGame();
        }

        addPlayerOrSpectator(room, playerId);

        System.out.println("Player " + playerId + " joined room " + roomCode);

        if(room.checkFull()) {
            GameStartDTO gameStartDTO = GameStartDTO.builder()
                    .roomCode(roomCode)
                    .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                    .build();

//            for (Player player : room.getPlayers()) {
//                messagingTemplate.convertAndSendToUser(player.getPlayerId(), "/topic/game-start/" + roomCode, gameStartDTO);
//            }
            // Nếu phòng đã đủ người chơi thì bắt đầu trò chơi
            messagingTemplate.convertAndSend("/topic/game-start/" + roomCode, gameStartDTO);
        }

        System.out.println("Players:");
        for (Player player : room.getPlayers()) {
            System.out.println(player.getPlayerId());
        }

        System.out.println("Spectators:");
        for (Player player : room.getSpectators()) {
            System.out.println(player.getPlayerId());
        }

        // Return a response with the room details
        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }

    private void addPlayerOrSpectator(Room room, String playerId) {
        Player player = new Player(playerId);
        if (!room.checkPlayerExist(playerId)) {
            if (!room.addPlayer(player) && !room.checkSpectatorExist(playerId)) {
                room.addSpectator(player);
            }
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestParam("userId") String userId,
                                        @RequestParam("firstMoveOption") FirstMoveOption firstMoveOption
    ) {

        System.out.println("Create room" + userId + firstMoveOption);

        String codeRandom = randomRoomCode();
        while (gameRoomManager.checkRoomExist(codeRandom)) {
            codeRandom = randomRoomCode();
        }
        Room room = gameRoomManager.createRoom(codeRandom, userId, firstMoveOption);

        // Add the owner to the room
        room.addPlayer(new Player(userId));


        System.out.println("Room created: " + room.getRoomCode());

        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }


    private String randomRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}


