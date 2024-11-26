package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.services.AIGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/ai")
@RestController
public class AIGameController {
//    private final AIGameService aiGameService;
    private AIGameService aiGameService;
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestParam String roomCode) {
        aiGameService = new AIGameService(true);
        return ResponseEntity.ok(roomCode);
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestBody GameMove gameMove) {
        return ResponseEntity.ok(aiGameService.playGame(gameMove.getRow(), gameMove.getCol()));
    }

    @PostMapping("/leave")
    public boolean leave() {
        aiGameService = null;
        return true;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create() {
        aiGameService = new AIGameService(false);
        return ResponseEntity.ok(aiGameService.getRoomCode());
    }
}
