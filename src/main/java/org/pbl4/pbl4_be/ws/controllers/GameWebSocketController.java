package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.PlayAgainCode;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.*;
import org.pbl4.pbl4_be.ws.services.WSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.pbl4.pbl4_be.Constants.GAME_PROGRESS_TOPIC;
import static org.pbl4.pbl4_be.Constants.PLAY_AGAIN_TOPIC;

@Controller
public class GameWebSocketController {
    private final WSService wsService;

    private final RoomDBService roomDBService;
    private final SimpMessagingTemplate messagingTemplate;

    private final PlayerSeasonService playerSeasonService;
    private final SeasonService seasonService;
    private final UserService userService;


    @Autowired
    public GameWebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, WSService wsService, RoomDBService roomDBService, SimpMessagingTemplate messagingTemplate1, PlayerSeasonService playerSeasonService, SeasonService seasonService, UserService userService) {
        this.wsService = wsService;
        this.roomDBService = roomDBService;
        this.messagingTemplate = messagingTemplate1;
        this.playerSeasonService = playerSeasonService;
        this.seasonService = seasonService;
        this.userService = userService;
    }

    @MessageMapping("/move/{roomCode}")
    @SendTo(GAME_PROGRESS_TOPIC + "{roomCode}")
    public GameMove makeMove(@DestinationVariable String roomCode, @Payload GameMove move) {
        // Xử lý nước đi của người chơi
        Room room = GameRoomManager.getInstance().getRoom(roomCode);

        Game game = room.getGamePlaying();
        move.setDuration((int) Duration.between(game.getStartTimeMove(), LocalDateTime.now()).getSeconds());
        game.getMoveList().add(move);
//        game.resetRemainMoveTime(move.getPlayerTurnId());

        if (game.processMove(move))
            move.setWin(true);

        game.increaseMoveCnt();
        if (move.isWin()) {
            if (move.getNthMove() % 2 == 0) {
                game.setWinnerId(game.getFirstPlayerId());
                room.increaseScore(game.getFirstPlayerId());
            } else {
                game.setWinnerId(game.getSecondPlayerId());
                room.increaseScore(game.getSecondPlayerId());
            }
            Season season = seasonService.findCurrentSeason().orElse(null);
            if(season != null) {
                PlayerSeason playerSeason1 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getFirstPlayerId()).orElse(new PlayerSeason(userService.findById(game.getFirstPlayerId()).orElse(null), season));
                PlayerSeason playerSeason2 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getSecondPlayerId()).orElse(new PlayerSeason(userService.findById(game.getSecondPlayerId()).orElse(null), season));
                playerSeason1.updateScore(game.getFirstPlayerId().equals(game.getWinnerId()), false);
                playerSeason2.updateScore(game.getSecondPlayerId().equals(game.getWinnerId()), false);
                if(playerSeason1.getWinStreak() != 0){
                    playerSeason1.bonusScoreTime(game.getFirstPlayerInfo().getRemainTime(), game.getSecondPlayerInfo().getRemainTime());
                }else if(playerSeason2.getWinStreak() != 0){
                    playerSeason2.bonusScoreTime(game.getSecondPlayerInfo().getRemainTime(), game.getFirstPlayerInfo().getRemainTime());
                }
                room.updateSeasonScore(playerSeason1);
                room.updateSeasonScore(playerSeason2);
                playerSeasonService.save(playerSeason1);
                playerSeasonService.save(playerSeason2);
            }
            room.setPlayerIsReady(false);
            setGameEnd(room, game);
            wsService.sendGameEndMessage(roomCode, game.getWinnerId());
            if(roomDBService.FindById(room.getRoomId()) != null) {
                RoomDB roomDB = roomDBService.FindById(room.getRoomId());
                roomDB.addGame(game);
                roomDBService.save(roomDB);
            }
        }

        if (game.getBoard().isFull()) {
            setGameEnd(room, game);
            room.increaseScore(game.getFirstPlayerId());
            room.increaseScore(game.getSecondPlayerId());
            room.setPlayerIsReady(false);
            Season season = seasonService.findCurrentSeason().orElse(null);
            if(season != null) {
                PlayerSeason playerSeason1 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getFirstPlayerId()).orElse(new PlayerSeason(userService.findById(game.getFirstPlayerId()).orElse(null), season));
                PlayerSeason playerSeason2 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getSecondPlayerId()).orElse(new PlayerSeason(userService.findById(game.getSecondPlayerId()).orElse(null), season));
                playerSeason1.updateScore(false, true);
                playerSeason2.updateScore(false, true);
                room.updateSeasonScore(playerSeason1);
                room.updateSeasonScore(playerSeason2);
                playerSeasonService.save(playerSeason1);
                playerSeasonService.save(playerSeason2);
            }
            if(roomDBService.FindById(room.getRoomId()) != null) {
                RoomDB roomDB = roomDBService.FindById(room.getRoomId());
                roomDB.addGame(game);
                roomDBService.save(roomDB);
            }
            wsService.sendGameEndMessage(roomCode, null);
        }

        return move;
    }

    private void setGameEnd(Room room, Game game) {
        game.setGameStatus(GameStatus.ENDED);
        game.setEndTime(LocalDateTime.now());
    }

    @MessageMapping("/play-again/{roomCode}")
    @SendTo(PLAY_AGAIN_TOPIC + "{roomCode}")
    public ResponseEntity<?> playAgain(@DestinationVariable String roomCode, @Payload PlayAgainRequest request) {
        Room room = GameRoomManager.getInstance().getRoom(roomCode);
        Game game = room.getLastGame();
        Player player = room.getPlayerById(request.getPlayerId());

        if (game.getGameStatus() == GameStatus.ENDED) {
            room.addGame();
            if(player != null) {
                player.setReady(true);
            }
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN).
                    build());
        } else {
            room.setPlayerIsReady(true);
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN_ACCEPT).
                    build());
        }
    }

    @MessageMapping("/winner/{roomCode}")
    @SendTo("/topic/winner/{roomCode}")
    public ResponseEntity<?> getWinner(@DestinationVariable String roomCode, @Payload Long winnerId) {
        Room room = GameRoomManager.getInstance().getRoom(roomCode);
        Game game = room.getLastGame();

        game.setWinnerId(winnerId);
        game.setGameStatus(GameStatus.ENDED);

        return ResponseEntity.ok(winnerId);
    }


    @MessageMapping("/message/{roomCode}")
    public void sendMessage(@DestinationVariable String roomCode, @Payload Message message) {
        Room room = GameRoomManager.getInstance().getRoom(roomCode);

        message.setSendTime(LocalDateTime.now());
        room.addMessage(message);

        for(Player player : room.getPlayers()) {
            if(player.getId().equals(message.getSenderId())) continue;

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(player.getId()),
                    "/queue/message/" + roomCode,
                    message
            );
        }
    }

}
