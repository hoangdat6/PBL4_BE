package org.pbl4.pbl4_be.controller;

import org.pbl4.pbl4_be.controller.dto.GameState;
import org.pbl4.pbl4_be.controller.dto.RoomResponse;
import org.pbl4.pbl4_be.controller.exception.BadRequestException;
import org.pbl4.pbl4_be.controller.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.models.Player;
import org.pbl4.pbl4_be.models.Room;
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

        if(room.checkPlayerExist(playerId)) {
            // Player đã tham gia phòng
            GameState gameState = GameState.builder()
                    .roomCode(roomCode)
                    .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                    .nthMove(room.getGamePlaying().getNthMove())
                    .build();

            gameState.setBoardState(room.getGamePlaying().getBoard());
            messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, gameState);
        }else {
            addPlayerOrSpectator(room, playerId);
            if(room.checkFull()) {
                GameState gameState = GameState.builder()
                        .roomCode(roomCode)
                        .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                        .nthMove(room.getGamePlaying().getNthMove())
                        .build();

                gameState.setBoardState(room.getGamePlaying().getBoard());

                messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, gameState);
            }
        }

        // show player list
        System.out.println("Player list: ");
        for (Player player : room.getPlayers()) {
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

        System.out.println("Create room " + userId + " " + firstMoveOption);

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

    // test get GameState
    @GetMapping("/game-state")
    public ResponseEntity<?> getGameState(@RequestParam("roomCode") String roomCode) {
        Room room = gameRoomManager.getRoom(roomCode);
        GameState gameState = GameState.builder()
                .roomCode(roomCode)
                .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                .nthMove(room.getGamePlaying().getNthMove())
                .build();
        gameState.setBoardState(room.getGamePlaying().getBoard());
        return ResponseEntity.status(HttpStatus.OK).body(gameState);
    }


    private String randomRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveRoom(@RequestParam("playerId") String playerId) {
        String roomCode = gameRoomManager.getRoomCodeByPlayerId(playerId);
        if (roomCode == null) {
            throw new BadRequestException("Player is not in any room");
        }

        Room room = gameRoomManager.getRoom(roomCode);
        if (room == null) {
            throw new BadRequestException("Room not found");
        }

        if (room.checkPlayerExist(playerId)) {
            room.removePlayer(playerId);
        } else if (room.checkSpectatorExist(playerId)) {
            room.removeSpectator(playerId);
        } else {
            throw new BadRequestException("Player is not in any room");
        }

        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }
}


