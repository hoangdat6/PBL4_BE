package org.pbl4.pbl4_be.ws.controller;

import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.PlayAgainCode;
import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {
    private final GameRoomManager gameRoomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/move/{roomCode}")
    @SendTo("/topic/game-progress/{roomCode}")
    public GameMove makeMove(@DestinationVariable String roomCode, @Payload GameMove move) {
        // Xử lý nước đi của người chơi
        Room room = gameRoomManager.getRoom(roomCode);

        Game game = room.getGamePlaying();
        game.getMoveList().add(move);
        if (game.processMove(move))
            move.setWin(true);

        game.increaseMoveCnt();
        if (move.isWin()) {
            if (move.getNthMove() % 2 == 0) {
                game.setWinnerId(game.getFirstPlayerId());
            } else {
                game.setWinnerId(game.getSecondPlayerId());
            }

            game.setGameStatus(GameStatus.ENDED);
            messagingTemplate.convertAndSend("/topic/game-end/" + roomCode, ResponseEntity.ok(game.getWinnerId()));
        }

        if (game.getBoard().isFull()) {
            game.setGameStatus(GameStatus.ENDED);
            messagingTemplate.convertAndSend("/topic/game-end/" + roomCode, ResponseEntity.ok(null));
        }

        return move;
    }


    @MessageMapping("/play-again/{roomCode}")
    @SendTo("/topic/play-again/{roomCode}")
    public ResponseEntity<?> playAgain(@DestinationVariable String roomCode, @Payload PlayAgainRequest request) {
        Room room = gameRoomManager.getRoom(roomCode);
        Game game = room.getLastGame();

        if (game.getGameStatus() == GameStatus.ENDED) {
            room.addGame();
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN).
                    build());
        } else {
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN_ACCEPT).
                    build());
        }
    }
}