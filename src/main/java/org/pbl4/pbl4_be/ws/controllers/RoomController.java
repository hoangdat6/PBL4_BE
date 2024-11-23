package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.controllers.dto.*;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.controllers.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.ParticipantType;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.services.RoomDBService;
import org.pbl4.pbl4_be.services.UserService;
import org.pbl4.pbl4_be.ws.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final GameRoomManager gameRoomManager;
    private final SimpMessagingTemplate messagingTemplate;
    private Logger logger = Logger.getLogger(RoomController.class.getName());
    private final UserService userService;
    private final MessagingService messagingService;

    private final RoomDBService roomDBService;

    @Autowired
    public RoomController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, MessagingService messagingService, UserService userService, RoomDBService roomDBService) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.messagingService = messagingService;
        this.roomDBService = roomDBService;
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
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT, "Player is playing in another room", roomCodeOfPlayer);
        }

        // Room này đảm bảo đã tồn tại
        Room room = gameRoomManager.getRoom(roomCode);

        //  nếu phòng chưa full thì thêm player vào phòng
        if (!room.checkFull() && !room.checkPlayerExist(userId)) {
            User user = userService.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));

            room.addPlayer(Player.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .avatar(user.getAvatar())
                    .score((byte) 0)
                    .isReady(true)
                    .rank(1)
                    .build()
            );
        }

        Game lastGame = room.getLastGame();
        if (room.checkFull() && room.isAllPlayerIsReady()) {
            /*
             * Nếu phòng đã full và game chưa bắt đầu thì bắt đầu game
             * Nếu game đang chờ chơi thì bắt đầu game
             * Nếu không thì gửi lại trạng thái của game trước đó
             */
            if (lastGame.getGameStatus() != GameStatus.STARTED) {
                System.out.println("Bắt đầu game");
                room.startGame();
                if(room.getRoomId() == null) {
                    RoomDB roomDB = new RoomDB(room);
                    roomDB = roomDBService.save(roomDB);
                    room.setRoomId(roomDB.getId());
                }
            }


            // Gửi lại trạng thái game trước đó

            // get thông tin người chơi

            Player player1 = room.getPlayers().get(0);
            Player player2 = room.getPlayers().get(1);

            GameState gameState = GameState.builder()
                    .roomCode(roomCode)
                    .startPlayerId(lastGame.getFirstPlayerInfo().getPlayerId())
                    .nthMove(lastGame.getNthMove())
                    .lastMove(lastGame.getLastMove())
                    .gameConfig(room.getGameConfig())
                    .build();

            if(Objects.equals(player1.getId(), lastGame.getFirstPlayerId())) {
                gameState.setPlayer1Info(new PlayerForGameState(player1, lastGame.getFirstPlayerInfo()));
                gameState.setPlayer2Info(new PlayerForGameState(player2, lastGame.getSecondPlayerInfo()));
            } else {
                gameState.setPlayer1Info(new PlayerForGameState(player2, lastGame.getSecondPlayerInfo()));
                gameState.setPlayer2Info(new PlayerForGameState(player1, lastGame.getFirstPlayerInfo()));
            }

            gameState.setBoardState(lastGame.getBoard());
            messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, gameState);
        }

        System.out.println("Số lượng game trong room: " + room.getGames().size());

        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomCode(roomCode)
                .participantType(ParticipantType.PLAYER)
                .isStarted(room.checkFull())
                .build();

        // Set participant type
        if (!room.checkPlayerExist(userId)) {
            response.setParticipantType(ParticipantType.SPECTATOR);
            room.addSpectator(Player.builder().id(userId).build());
        }

        // Return a response with the room details
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRoom(@RequestBody GameConfig gameConfig, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();

        // Kiểm tra player đã tham gia phòng khác chưa
        logger.info("User Id: " + userId + " Create room!");

        String roomCodeOfPlayer = gameRoomManager.getRoomCodeByPlayerId(userId);

        if (roomCodeOfPlayer != null) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT, "Player is playing in another room", roomCodeOfPlayer);
        }

        String codeRandom = randomRoomCode();
        while (gameRoomManager.checkRoomExist(codeRandom)) {
            codeRandom = randomRoomCode();
        }

        /*
         * Mặc định tạo game lúc tạo phòng với người chơi đầu tiên là người tạo phòng
         * Game này sẽ được bắt đầu khi phòng đã đủ người chơi
         */
        Room room = gameRoomManager.createRoom(codeRandom, gameConfig);

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

        String playerRoomCode = gameRoomManager.getRoomCodeByPlayerId(userId);
        String spectatorRoomCode = gameRoomManager.getRoomCodeBySpectatorId(userId);

        String roomCode = playerRoomCode != null ? playerRoomCode : spectatorRoomCode;

        if (playerRoomCode == null && spectatorRoomCode == null) {
            logger.warning("Player with ID: " + userId + " is not in any room");
            throw new BadRequestException("Player is not in any room");
        }

        Room room = gameRoomManager.getRoom(roomCode);


        if (room == null) {
            throw new BadRequestException("Room not found");
        }

        logger.info("Số lượng phòng trong hệ thống: " + gameRoomManager.getRoomsSize());

        /*
         * Có 3 trường hợp xảy ra:
         * 1. Người chơi vừa tạo phòng và không có người chơi nào khác thì xóa phòng
         * 2. Người chơi đang chơi game và rời phòng -> lưu lại kết quả, kết thúc game, thông báo người thắng
         * 3. Ván đấu kết thúc và người chơi rời phòng
         */
        // 1
        if (room.getPlayers().size() == 1) {
            gameRoomManager.removeRoom(roomCode);
            logger.info("Số lượng phòng trong hệ thống: " + gameRoomManager.getRoomsSize());
            return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(roomCode).build());
        }

        // 2
        if (room.checkPlayerExist(userId)) {
            room.removePlayer(userId);
            Game gamePlaying = room.getGamePlaying();
            if (gamePlaying != null && gamePlaying.getGameStatus() == GameStatus.STARTED) {
                gamePlaying.setWinnerId(gamePlaying.getFirstPlayerId().equals(userId) ? gamePlaying.getSecondPlayerId() : gamePlaying.getFirstPlayerId());
                gamePlaying.setGameStatus(GameStatus.ENDED);
                if(roomDBService.FindById(room.getRoomId()) != null) {
                    RoomDB roomDB = roomDBService.FindById(room.getRoomId());
                    roomDB.addGame(gamePlaying);
                    roomDBService.save(roomDB);
                }
                // send game end message
                messagingService.sendGameEndMessage(roomCode, gamePlaying.getWinnerId());
            }
        } else if (room.checkSpectatorExist(userId)) {
            room.removeSpectator(userId);
        } else {
            throw new BadRequestException("Player is not in any room");
        }

        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }

    // get lich su choi game
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();
        List<RoomDTO> list = roomDBService.getHistory(userId);
        List<HistoryDTO> result = new ArrayList<>();
        for(RoomDTO roomDTO : list){
            User player1 = userService.findById(roomDTO.getPlayer1Id()).orElseThrow(() -> new BadRequestException("User not found"));
            User player2 = userService.findById(roomDTO.getPlayer2Id()).orElseThrow(() -> new BadRequestException("User not found"));
            HistoryDTO historyDTO = new HistoryDTO(roomDTO, player1, player2);
            for(GameDTO gameDTO : roomDTO.getGames()){
                historyDTO.UpdateScore(gameDTO.getWinnerId());
            }
            result.add(historyDTO);
        }
        return ResponseEntity.ok(result);
    }

}


