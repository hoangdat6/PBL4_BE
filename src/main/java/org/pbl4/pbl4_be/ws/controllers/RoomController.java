package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.controllers.dto.ConfigGameDTO;
import org.pbl4.pbl4_be.controllers.dto.JoinRoomResponse;
import org.pbl4.pbl4_be.controllers.dto.GameState;
import org.pbl4.pbl4_be.controllers.dto.RoomResponse;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.controllers.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.ParticipantType;
import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Player;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.models.UserDetailsImpl;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.ws.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MessagingService messagingService;
    @Autowired
    public RoomController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, MessagingService messagingService) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
        this.messagingService = messagingService;
    }

    // Xử lý yêu cầu tham gia phòng
    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestParam("roomCode") String roomCode, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();
        if (gameRoomManager.getRoom(roomCode) == null) {
            throw new BadRequestException("Room not found");
        }

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(userId);

        // Kiểm tra player đã tham gia phòng khác chưa
        if (roomCodeOfPlayer != null && !Objects.equals(roomCode, roomCodeOfPlayer)) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT ,"Player is playing in another room", roomCodeOfPlayer);
        }

        // Room này đảm bảo đã tồn tại
        Room room = gameRoomManager.getRoom(roomCode);

        //  nếu phòng chưa full thì thêm player vào phòng
        if (!room.checkFull() && !room.checkPlayerExist(userId)) {
            room.addPlayer(new Player(userId));
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
        if (!room.checkPlayerExist(userId)) {
            response.setParticipantType(ParticipantType.SPECTATOR);
        }


        // Return a response with the room details
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRoom(@RequestBody ConfigGameDTO configGameDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();

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


    private String randomRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveRoom(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();
        String roomCode = gameRoomManager.getRoomCodeByPlayerId(userId);

        if (roomCode == null) {
            logger.warning("Player with ID: " + userId + " is not in any room");
            throw new BadRequestException("Player is not in any room");
        }

        Room room = gameRoomManager.getRoom(roomCode);
        if (room == null) {
            throw new BadRequestException("Room not found");
        }

        // nếu player mới tạo phòng thì xóa phòng
        if (room.getGamePlaying() == null) {
            gameRoomManager.removeRoom(roomCode);
            return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(roomCode).build());
        }

        if (room.checkPlayerExist(userId)) {
            room.removePlayer(userId);
            Game gamePlaying = room.getGamePlaying();
            if (gamePlaying != null && gamePlaying.getGameStatus() == GameStatus.STARTED) {
                gamePlaying.setWinnerId(gamePlaying.getFirstPlayerId().equals(userId) ? gamePlaying.getSecondPlayerId() : gamePlaying.getFirstPlayerId());
                gamePlaying.setGameStatus(GameStatus.ENDED);
                messagingService.sendGameEndMessage(roomCode, gamePlaying.getWinnerId());
            }
        } else if (room.checkSpectatorExist(userId)) {
            room.removeSpectator(userId);
        } else {
            throw new BadRequestException("Player is not in any room");
        }

        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }
}


