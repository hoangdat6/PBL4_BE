package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.models.UserDetailsImpl;
import org.pbl4.pbl4_be.services.AIGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequestMapping("/api/ai")
@RestController
public class AIGameController {
    private final Map<String, AIGameService> gameRoomManager = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();


    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestParam String roomCode, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        AIGameService aiGameService = gameRoomManager.get(roomCode);
        Long userId = currentUser.getId();
        if(aiGameService == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }else if(aiGameService.getPlayerId() != null) {
            if(!aiGameService.getPlayerId().equals(userId)) {
                return ResponseEntity.badRequest().body("Room is full");
            }
        }else {
            aiGameService.setPlayerId(userId);
        }
        return ResponseEntity.ok(roomCode);
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestParam String roomCode, @RequestBody GameMove gameMove) {
        AIGameService aiGameService = gameRoomManager.get(roomCode);
        if(aiGameService == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }
        if(aiGameService.getPlayerId() == null) {
            return ResponseEntity.badRequest().body("Room is empty");
        }
        if(aiGameService.getPlayerId().equals(currentUser.getId())) {
            try {
                // Sử dụng Callable để xử lý không đồng bộ và trả về GameMove
                Future<GameMove> future = executorService.submit(() ->
                        aiGameService.playGame(gameMove.getRow(), gameMove.getCol())
                );

                // Chờ kết quả từ Callable
                GameMove move = future.get();
                return ResponseEntity.ok(move);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error processing move: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Not your turn");
    }

    @PostMapping("/leave")
    public boolean leave(@RequestParam String roomCode, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        AIGameService aiGameService = gameRoomManager.get(roomCode);
        if(aiGameService == null) {
            return false;
        }
        if(aiGameService.getPlayerId() == null) {
            return false;
        }
        if(aiGameService.getPlayerId().equals(currentUser.getId())) {
            aiGameService.setPlayerId(null);
        }
        if(aiGameService.getPlayerId() == null) {
            gameRoomManager.remove(roomCode);
        }
        return true;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create() {
        System.out.println(gameRoomManager.size());
        AIGameService aiGameService = new AIGameService(true);
        String roomCode = aiGameService.getRoomCode();
        gameRoomManager.put(roomCode, aiGameService);
        System.out.println("New size: " + gameRoomManager.size());
        return ResponseEntity.ok(roomCode);
    }
}