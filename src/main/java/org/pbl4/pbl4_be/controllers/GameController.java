package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/game/")
public class GameController {
    private final GameRoomManager gameRoomManager;
    private final GameService gameService;

    public GameController(GameRoomManager gameRoomManager, GameService gameService) {
        this.gameRoomManager = gameRoomManager;
        this.gameService = gameService;
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

    // Bảng xếp hạng
    @PostMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(@RequestParam("rankings") String rankings, @RequestParam("pageNumber") int pageNumber) {
        if(pageNumber < 0){
            return ResponseEntity.badRequest().body("Page number must be greater than 0");
        }
        return ResponseEntity.ok(gameService.getLeaderboard(rankings, pageNumber));
     }



}
