package org.pbl4.pbl4_be.controller;

import org.pbl4.pbl4_be.controller.dto.ConfigGameDTO;
import org.pbl4.pbl4_be.controller.dto.GameState;
import org.pbl4.pbl4_be.controller.dto.JoinRoomResponse;
import org.pbl4.pbl4_be.controller.dto.RoomResponse;
import org.pbl4.pbl4_be.controller.exception.BadRequestException;
import org.pbl4.pbl4_be.controller.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.enums.ParticipantType;
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
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final GameRoomManager gameRoomManager;
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

        logger.info("User Id: " + playerId + " Join room: " + roomCode);

        if (gameRoomManager.getRoom(roomCode) == null) {
            logger.warning("Room" + roomCode + "not found");
            throw new BadRequestException("Room not found");
        }

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(playerId);

        // Kiểm tra player đã tham gia phòng khác chưa
        if (roomCodeOfPlayer != null && !Objects.equals(roomCode, roomCodeOfPlayer)) {
            logger.warning("Player with ID: " + playerId + " is playing in another room " + roomCodeOfPlayer);
            throw new PlayerAlreadyInRoomException("Player is playing in another room");
        }


        // Room này đảm bảo đã tồn tại
        Room room = gameRoomManager.getRoom(roomCode);

        //  nếu phòng chưa full thì thêm player vào phòng
        if (!room.checkFull()) {
            room.addPlayer(new Player(playerId));

        }


        if (room.checkFull()) {
            room.startGame();

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


//    @PostMapping("/create")
//    public ResponseEntity<?> createRoom(@RequestParam("userId") String userId,
//                                        @RequestParam("firstMoveOption") FirstMoveOption firstMoveOption
//    ) {
//        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(userId);
//
//        // Kiểm tra player đã tham gia phòng khác chưa
//        logger.info("User Id: " + userId + " Create room!");
////        if (roomCodeOfPlayer != null) {
//////            gameRoomManager.removeRoom(roomCodeOfPlayer);
////            // nếu user đã ở trong phòng thì thông báo cho các client khác rằng user đã rời phòng
////            Room room = gameRoomManager.getRoom(roomCodeOfPlayer);
////            if (room != null) {
////                room.setEnd();
////                messagingTemplate.convertAndSend("/topic/leave-room/" + roomCodeOfPlayer, RoomResponse.builder().roomCode(roomCodeOfPlayer).build());
////            }
////        }
//        String codeRandom = randomRoomCode();
//        while (gameRoomManager.checkRoomExist(codeRandom)) {
//            codeRandom = randomRoomCode();
//        }
//        Room room = gameRoomManager.createRoom(codeRandom, userId, firstMoveOption);
//
//        logger.info("Room created with code: " + room.getRoomCode());
//        // Add the owner to the room
//
//        room.addPlayer(new Player(userId));
//        JoinRoomResponse response = JoinRoomResponse.builder()
//                .roomCode(room.getRoomCode())
//                .participantType(ParticipantType.PLAYER)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody ConfigGameDTO configGameDTO) {
        String userId = configGameDTO.getOwnerId();

        // Kiểm tra player đã tham gia phòng khác chưa
        logger.info("User Id: " + userId + " Create room!");

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
            logger.warning("Room" + roomCode + "not found");
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

