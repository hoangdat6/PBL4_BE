package org.pbl4.pbl4_be.controller;

import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.service.GameRoomManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/game/")
public class GameController {
    private final GameRoomManager gameRoomManager;

    public GameController(GameRoomManager gameRoomManager) {
        this.gameRoomManager = gameRoomManager;
    }

    // Xử lý yêu cầu tham gia phòng
    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestParam("roomId") String roomId) {
        Room room = gameRoomManager.getRoom(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        Game game = room.addGame();

        if (game == null) {
            return ResponseEntity.badRequest().body("Room is full");
        }

        return ResponseEntity.ok(game.getGameId());
    }
}
