package org.pbl4.pbl4_be.ws.controller;

import org.pbl4.pbl4_be.controller.dto.ConfigGameDTO;
import org.pbl4.pbl4_be.controller.dto.JoinRoomResponse;
import org.pbl4.pbl4_be.controllers.dto.GameState;
import org.pbl4.pbl4_be.controllers.dto.RoomResponse;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.controllers.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.ParticipantType;
import org.pbl4.pbl4_be.models.Player;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final org.pbl4.pbl4_be.services.GameRoomManager gameRoomManager;
    private final SimpMessagingTemplate messagingTemplate;
    private Logger logger = Logger.getLogger(RoomController.class.getName());

    @Autowired
    public RoomController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
    }

    // Xử lý yêu cầu tham gia phòng
    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestParam("playerId") String playerId,
                                       @RequestParam("roomCode") String roomCode) {

        if (gameRoomManager.getRoom(roomCode) == null) {
            throw new BadRequestException("Room not found");
        }

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(playerId);

        // Kiểm tra player đã tham gia phòng khác chưa
        if (roomCodeOfPlayer != null && !Objects.equals(roomCode, roomCodeOfPlayer)) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT ,"Player is playing in another room", roomCodeOfPlayer);
        }

        // Room này đảm bảo đã tồn tại
        Room room = gameRoomManager.getRoom(roomCode);

        //  nếu phòng chưa full thì thêm player vào phòng
        if (!room.checkFull() && !room.checkPlayerExist(playerId)) {
            room.addPlayer(new Player(playerId));
        }

        if (room.checkFull() && room.getGamePlaying() == null) {
            room.startGame();
        }   

        System.out.println("Số lượng game trong room: " + room.getGames().size());

        if(room.getGamePlaying() != null) {
            GameState gameState = GameState.builder()
                    .roomCode(roomCode)
                    .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                    .nthMove(room.getGamePlaying().getNthMove())
                    .build();
            gameState.setBoardState(room.getGamePlaying().getBoard());

            messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, gameState);
        }

        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomCode(roomCode)
                .participantType(ParticipantType.PLAYER)
                .isStarted(room.checkFull())
                .build();

        // Set participant type
        if (!room.checkPlayerExist(playerId)) {
            response.setParticipantType(ParticipantType.SPECTATOR);
        }


        // Return a response with the room details
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody ConfigGameDTO configGameDTO) {
        String userId = configGameDTO.getOwnerId();

        // Kiểm tra player đã tham gia phòng khác chưa
        logger.info("User Id: " + userId + " Create room!");

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(userId);

        if (roomCodeOfPlayer != null) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT ,"Player is playing in another room", roomCodeOfPlayer);
        }

        String codeRandom = randomRoomCode();
        while (gameRoomManager.checkRoomExist(codeRandom)) {
            codeRandom = randomRoomCode();
        }
        Room room = gameRoomManager.createRoom(codeRandom, configGameDTO);

        logger.info("Room created with code: " + room.getRoomCode());
        // Add the owner to the room

        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomCode(room.getRoomCode())
                .participantType(ParticipantType.PLAYER)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
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
            logger.warning("Player with ID: " + playerId + " is not in any room");
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


