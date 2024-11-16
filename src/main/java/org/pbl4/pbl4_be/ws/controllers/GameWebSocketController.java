package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.enums.GameStatus;
import org.pbl4.pbl4_be.enums.PlayAgainCode;
import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.ws.services.MessagingService;
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
    private final MessagingService messagingService;

    @Autowired
    public GameWebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, MessagingService messagingService) {
        this.gameRoomManager = gameRoomManager;
        this.messagingService = messagingService;
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

            setGameEnd(room, game);
            messagingService.sendGameEndMessage(roomCode, game.getWinnerId());
        }

        if (game.getBoard().isFull()) {
            setGameEnd(room, game);
            messagingService.sendGameEndMessage(roomCode, null);
        }

        return move;
    }

    private void setGameEnd(Room room, Game game) {
        game.setGameStatus(GameStatus.ENDED);
    }

    @MessageMapping("/play-again/{roomCode}")
    @SendTo("/topic/play-again/{roomCode}")
    public ResponseEntity<?> playAgain(@DestinationVariable String roomCode, @Payload PlayAgainRequest request) {
        Room room = gameRoomManager.getRoom(roomCode);
        Game game = room.getLastGame();

        if (game.getGameStatus() == GameStatus.ENDED) {
            room.addGame();
            room.getLastGame().setGameStatus(GameStatus.PENDING);
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN).
                    build());
        } else {
            return ResponseEntity.ok(PlayAgainResponse.builder().playerId(request.getPlayerId()).roomCode(roomCode).code(PlayAgainCode.PLAY_AGAIN_ACCEPT).
                    build());
        }
    }
}
