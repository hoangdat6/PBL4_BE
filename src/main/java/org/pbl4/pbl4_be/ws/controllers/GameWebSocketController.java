package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.PlayAgainCode;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.services.RoomDBService;
import org.pbl4.pbl4_be.ws.services.MessagingService;
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

@Controller
public class GameWebSocketController {
    private final GameRoomManager gameRoomManager;
    private final MessagingService messagingService;

    private final RoomDBService roomDBService;


    @Autowired
    public GameWebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, MessagingService messagingService, RoomDBService roomDBService) {
        this.gameRoomManager = gameRoomManager;
        this.messagingService = messagingService;
        this.roomDBService = roomDBService;
    }

    @MessageMapping("/move/{roomCode}")
    @SendTo("/topic/game-progress/{roomCode}")
    public GameMove makeMove(@DestinationVariable String roomCode, @Payload GameMove move) {
        // Xử lý nước đi của người chơi
        Room room = gameRoomManager.getRoom(roomCode);

        Game game = room.getGamePlaying();
        move.setDuration((int) Duration.between(game.getStartTimeMove(), LocalDateTime.now()).getSeconds());
        game.getMoveList().add(move);
        game.resetRemainMoveTime(move.getPlayerTurnId());

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
            room.setPlayerIsReady(false);
            setGameEnd(room, game);
            messagingService.sendGameEndMessage(roomCode, game.getWinnerId());
            if(roomDBService.FindById(room.getRoomId()) != null) {
                RoomDB roomDB = roomDBService.FindById(room.getRoomId());
                roomDB.addGame(game);
                roomDBService.save(roomDB);
            }
        }

        if (game.getBoard().isFull()) {
            setGameEnd(room, game);
            messagingService.sendGameEndMessage(roomCode, null);
        }

        return move;
    }

    private void setGameEnd(Room room, Game game) {
        game.setGameStatus(GameStatus.ENDED);
        game.setEndTime(LocalDateTime.now());
    }

    @MessageMapping("/play-again/{roomCode}")
    @SendTo("/topic/play-again/{roomCode}")
    public ResponseEntity<?> playAgain(@DestinationVariable String roomCode, @Payload PlayAgainRequest request) {
        Room room = gameRoomManager.getRoom(roomCode);
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
}
