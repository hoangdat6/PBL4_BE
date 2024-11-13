package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.services.AIGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/ai")
@RestController
public class AIGameController {
//    private final AIGameService aiGameService;
    private AIGameService aiGameService;
    @PostMapping("/join")
    public boolean join() {
        aiGameService = new AIGameService(true);
        return true;
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestBody GameMove gameMove) {
        return ResponseEntity.ok(aiGameService.playGame(gameMove.getRow(), gameMove.getCol()));
    }
}
