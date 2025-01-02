package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.Game;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/game/")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

//    // Xử lý yêu cầu tham gia phòng
//    @PostMapping("/create")
//    public ResponseEntity<?> createGame(@RequestParam("roomId") String roomId) {
//        Room room = GameRoomManager.getInstance().getRoom(roomId);
//
//        if (room == null) {
//            return ResponseEntity.badRequest().body("Room not found");
//        }
//
//        Game game = room.addGame();
//
//        if (game == null) {
//            return ResponseEntity.badRequest().body("Room is full");
//        }
//
//        return ResponseEntity.ok(game.getGameId());
//    }

    // Bảng xếp hạng
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @RequestParam("rankings") String rankings,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        page -= 1;
        if (page < 0) {
            return ResponseEntity.badRequest().body("Page number must be greater than 0");
        }
        return ResponseEntity.ok(gameService.getLeaderboard(rankings, page, pageSize));
    }
}
