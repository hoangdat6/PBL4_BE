package org.pbl4.pbl4_be.controller;

import org.pbl4.pbl4_be.controller.dto.GameStartDTO;
import org.pbl4.pbl4_be.model.GameMove;
import org.pbl4.pbl4_be.model.Game;
import org.pbl4.pbl4_be.model.Room;
import org.pbl4.pbl4_be.service.GameRoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameWebSocketController {
    private final GameRoomManager gameRoomManager;

    @Autowired
    public GameWebSocketController(GameRoomManager gameRoomManager) {
        this.gameRoomManager = gameRoomManager;
    }

    @MessageMapping("/move/{roomCode}")
    @SendTo("/topic/game-progress/{roomCode}")
    public GameMove makeMove(@PathVariable String roomCode, @Payload GameMove move) {
        // Xử lý nước đi của người chơi
        Room room = gameRoomManager.getRoom(roomCode);
        Game game = room.getGamePlaying();
        if(game.processMove(move))
            move.setWin(true);

        game.increaseMoveCnt();
        if(move.isWin()) {
            System.out.println("Player " + move.getNthMove() % 2 + " win");
            game.setIsWin(true);
        }
        return move;
    }

    @MessageMapping("/start/{roomCode}")
    @SendTo("/game-start/{roomCode}")
    public GameStartDTO startGame(@PathVariable String roomCode) {
        // Logic bắt đầu trò chơi
        Room room = gameRoomManager.getRoom(roomCode);
        GameStartDTO gameStartDTO = GameStartDTO.builder()
                .roomCode(roomCode)
                .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                .build();
        return gameStartDTO;
    }

}
