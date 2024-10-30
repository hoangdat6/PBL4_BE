package org.pbl4.pbl4_be.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pbl4.pbl4_be.controller.dto.GameState;
import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.service.GameRoomManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Map;

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
        if(game.processMove(move))
            move.setWin(true);


        game.increaseMoveCnt();
        if(move.isWin()) {
            if(move.getNthMove() % 2 == 0) {
                game.setWinnerId(game.getFirstPlayerId());
            } else {
                game.setWinnerId(game.getSecondPlayerId());
            }
        }
        return move;
    }

    @MessageMapping("/start/{roomCode}")
    @SendTo("/topic/game-start/{roomCode}")
    public GameState startGame(@PathVariable String roomCode) {
        // Logic bắt đầu trò chơi
        Room room = gameRoomManager.getRoom(roomCode);
        GameState gameStartDTO = GameState.builder()
                .roomCode(roomCode)
                .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                .build();
        return gameStartDTO;
    }

    @MessageMapping("/join-room")
    public void handleJoinRoom(Principal principal, @RequestParam("roomCode") String roomCode) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(roomCode, Map.class);
        // Lấy room từ gameRoomManager
        Room room = gameRoomManager.getRoom(map.get("roomCode"));
        System.out.println("Join room " + " " + roomCode);

        // Kiểm tra phòng đã đầy hay chưa
        if (room != null && room.checkFull()) {
            // Tạo DTO để thông báo bắt đầu game
            GameState gameStartDTO = GameState.builder()
                    .roomCode(room.getRoomCode())
                    .startPlayerId(room.getGamePlaying().getFirstPlayerId())
                    .build();

            // Gửi thông báo tới tất cả người chơi trong phòng
            messagingTemplate.convertAndSend("/topic/game-start/" + roomCode, gameStartDTO);
        }
    }

}
