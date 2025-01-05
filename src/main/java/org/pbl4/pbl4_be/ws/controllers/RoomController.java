package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.controllers.dto.*;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.controllers.exception.PlayerAlreadyInRoomException;
import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.ParticipantType;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.*;
import org.pbl4.pbl4_be.ws.services.WSService;
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

import static org.pbl4.pbl4_be.Constants.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private Logger logger = Logger.getLogger(RoomController.class.getName());
    private final UserService userService;
    private final WSService messagingService;
    private final RoomDBService roomDBService;
    private final CalculateScoreService calculateScoreService;
    private final WSService wsService;

    @Autowired
    public RoomController(WSService messagingService, UserService userService, RoomDBService roomDBService, CalculateScoreService calculateScoreService, WSService wsService) {
        this.userService = userService;
        this.messagingService = messagingService;
        this.roomDBService = roomDBService;
        this.calculateScoreService = calculateScoreService;
        this.wsService = wsService;
    }

    // Xử lý yêu cầu tham gia phòng
    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestParam("roomCode") String roomCode, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();
        if (GameRoomManager.getInstance().getRoom(roomCode) == null) {
            throw new BadRequestException("Room not found");
        }

        String roomCodeOfPlayer = GameRoomManager.getInstance().getRoomCodeByPlayerId(userId);

        // Kiểm tra player đã tham gia phòng khác chưa
        if (roomCodeOfPlayer != null && !Objects.equals(roomCode, roomCodeOfPlayer)) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT, "Player is playing in another room", roomCodeOfPlayer);
        }

        // Room này đảm bảo đã tồn tại
        Room room = GameRoomManager.getInstance().getRoom(roomCode);

        //  nếu phòng chưa full thì thêm player vào phòng
        if (!room.checkFull() && !room.checkPlayerExist(userId)) {
            User user = userService.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));

            room.addPlayer(Player.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .avatar(user.getAvatar())
                    .matchScore((byte) 0)
                    .isReady(true)
                    .rank(1)
                    .build()
            );
        }

        Game lastGame = room.getLastGame();
        if (room.checkFull()) {
            /*
             * Nếu phòng đã full và game chưa bắt đầu thì bắt đầu game
             * Nếu game đang chờ chơi thì bắt đầu game
             * Nếu không thì gửi lại trạng thái của game trước đó
             */

            GameState gameState = null;

            if (lastGame.getGameStatus() != GameStatus.STARTED && room.isAllPlayerIsReady()) {
                System.out.println("Bắt đầu game");
                room.startGame();
                gameState = getGameStart(room);
                wsService.sendAllPlayers(room.getPlayers(), GAME_START_TOPIC + roomCode, gameState);
                if (room.getRoomId() == null) {
                    RoomDB roomDB = new RoomDB(room);
                    roomDB = roomDBService.save(roomDB);
                    room.setRoomId(roomDB.getId());
                }
            }

            if (gameState == null) {
                gameState = getGameStart(room);
            }

            wsService.sendToUser(userId, GAME_STATE_TOPIC + roomCode, gameState);
            wsService.sendToUser(userId, SPECTATORS_TOPIC + roomCode, room.getSpectators());
        }


        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomCode(roomCode)
                .participantType(ParticipantType.PLAYER)
                .isStarted(room.checkFull())
                .build();

        // Set participant type
        if (!room.checkPlayerExist(userId)) {
            response.setParticipantType(ParticipantType.SPECTATOR);
            User user = userService.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));

            if(!room.checkSpectatorExist(userId)) {
                room.addSpectator(Player.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .avatar(user.getAvatar())
                        .build());
                wsService.sendAllPlayers(room.getPlayers(), SPECTATORS_TOPIC + roomCode, room.getSpectators());
            }
        }

        // Return a response with the room details

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    private GameState getGameStart(Room room) {
        String roomCode = room.getRoomCode();
        Game lastGame = room.getLastGame();
        Player player1 = room.getPlayers().get(0);
        Player player2 = room.getPlayers().get(1);

        GameState gameState = GameState.builder()
                .roomCode(roomCode)
                .startPlayerId(lastGame.getFirstPlayerInfo().getPlayerId())
                .nthMove(lastGame.getNthMove())
                .lastMove(lastGame.getLastMove())
                .gameConfig(room.getGameConfig())
                .winnerId(lastGame.getWinnerId())
                .spectators(room.getSpectators())
                .messages(room.getMessages())
                .build();

        if (Objects.equals(player1.getId(), lastGame.getFirstPlayerId())) {
            gameState.setPlayer1Info(new PlayerForGameState(player1, lastGame.getFirstPlayerInfo()));
            gameState.setPlayer2Info(new PlayerForGameState(player2, lastGame.getSecondPlayerInfo()));
        } else {
            gameState.setPlayer1Info(new PlayerForGameState(player2, lastGame.getSecondPlayerInfo()));
            gameState.setPlayer2Info(new PlayerForGameState(player1, lastGame.getFirstPlayerInfo()));
        }

        gameState.setBoardState(lastGame.getBoard());

        return gameState;
    }


    /**
     * API này chỉ dùng để tạo phòng chơi với bạn bè
     * @param gameConfig
     * @param currentUser
     * @return
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createRoom(@RequestBody GameConfig gameConfig, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();

        // Kiểm tra player đã tham gia phòng khác chưa
        logger.info("User Id: " + userId + " Create room!");

        String roomCodeOfPlayer = GameRoomManager.getInstance().getRoomCodeByPlayerId(userId);

        if (roomCodeOfPlayer != null) {
            throw new PlayerAlreadyInRoomException(HttpStatus.CONFLICT, "Player is playing in another room", roomCodeOfPlayer);
        }

        String codeRandom = randomRoomCode();
        while (GameRoomManager.getInstance().checkRoomExist(codeRandom)) {
            codeRandom = randomRoomCode();
        }

        /*
         * Mặc định tạo game lúc tạo phòng với người chơi đầu tiên là người tạo phòng
         * Game này sẽ được bắt đầu khi phòng đã đủ người chơi
         */
        Room room = GameRoomManager.getInstance().createRoom(codeRandom, gameConfig, false);

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

        String playerRoomCode = GameRoomManager.getInstance().getRoomCodeByPlayerId(userId);
        String spectatorRoomCode = GameRoomManager.getInstance().getRoomCodeBySpectatorId(userId);

        String roomCode = playerRoomCode != null ? playerRoomCode : spectatorRoomCode;

        if (playerRoomCode == null && spectatorRoomCode == null) {
            logger.warning("Player with ID: " + userId + " is not in any room");
            throw new BadRequestException("Player is not in any room");
        }

        Room room = GameRoomManager.getInstance().getRoom(roomCode);

        if (room == null) {
            throw new BadRequestException("Room not found");
        }

        logger.info("Số lượng phòng trong hệ thống: " + GameRoomManager.getInstance().getRoomsSize());

        /*
         * Có 3 trường hợp xảy ra:
         * 1. Người chơi vừa tạo phòng và không có người chơi nào khác thì xóa phòng
         * 2. Người chơi đang chơi game và rời phòng -> lưu lại kết quả, kết thúc game, thông báo người thắng
         * 3. Ván đấu kết thúc và người chơi rời phòng
         */
        // 2
        if (room.checkPlayerExist(userId)) {
            room.removePlayer(userId);
            Game gamePlaying = room.getGamePlaying();
            if (gamePlaying != null && gamePlaying.getGameStatus() == GameStatus.STARTED) {
                gamePlaying.setWinnerId(gamePlaying.getFirstPlayerId().equals(userId) ? gamePlaying.getSecondPlayerId() : gamePlaying.getFirstPlayerId());
                gamePlaying.setGameStatus(GameStatus.ENDED);
                if (roomDBService.FindById(room.getRoomId()) != null) {
                    if(room.isPlayOnline()) calculateScoreService.updateScore(room, false);
                    RoomDB roomDB = roomDBService.FindById(room.getRoomId());
                    roomDB.addGame(gamePlaying);
                    roomDBService.save(roomDB);
                }
                // send game end message
                messagingService.sendGameEndMessage(roomCode, gamePlaying.getWinnerId());
            }
            GameRoomManager.getInstance().removeRoom(roomCode);
        } else if (room.checkSpectatorExist(userId)) {
            room.removeSpectator(userId);
            wsService.sendAllPlayers(room.getPlayers(), SPECTATORS_TOPIC + roomCode, room.getSpectators());
        } else {
            throw new BadRequestException("Player is not in any room");
        }

        return ResponseEntity.status(HttpStatus.OK).body(RoomResponse.builder().roomCode(room.getRoomCode()).build());
    }

}


