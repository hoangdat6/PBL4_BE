package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.controllers.dto.AIRoomState;
import org.pbl4.pbl4_be.controllers.dto.JoinAIRoomResponse;
import org.pbl4.pbl4_be.models.GameMove;
import org.pbl4.pbl4_be.models.User;
import org.pbl4.pbl4_be.models.UserDetailsImpl;
import org.pbl4.pbl4_be.services.AIGameService;
import org.pbl4.pbl4_be.services.UserService;
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
    private final UserService userService;

    public AIGameController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create() {
        System.out.println(gameRoomManager.size());
        AIGameService aiGameService = new AIGameService(true);
        String roomCode = aiGameService.getRoomCode();
        gameRoomManager.put(roomCode, aiGameService);
        return ResponseEntity.ok(roomCode);
    }

    /**
     * Join a room
     * @param currentUser
     * @param roomCode
     * @return
     */

    @PostMapping("/join")
    public ResponseEntity<?> join(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestParam String roomCode) {
        AIGameService aiGameService = gameRoomManager.get(roomCode);
        Long userId = currentUser.getId();
        User player = userService.findById(userId).orElse(null);

        if(aiGameService == null) {
            return ResponseEntity.notFound().build();
        }else if(aiGameService.getPlayerId() != null) {
            if(!aiGameService.getPlayerId().equals(userId)) {
                return ResponseEntity.badRequest().body("Room is full");
            }
        }else {
            aiGameService.setPlayerId(userId);
        }

        assert player != null;
        AIRoomState aiRoomState = AIRoomState.builder()
                .roomCode(roomCode)
                .playerId(userId)
                .playerName(player.getName())
                .playerAvatar(player.getAvatar())
                .lastMove(aiGameService.getLastMove())
                .board(aiGameService.getBoard())
                .nthMove(aiGameService.getNthMove())
                .isPlayerTurn(aiGameService.isPlayerTurn())
                .build();

        return ResponseEntity.ok(aiRoomState);
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
                // add move to game
                aiGameService.addMove(gameMove.getRow(), gameMove.getCol());
                aiGameService.setPlayerTurn(false);
                // Sử dụng Callable để xử lý không đồng bộ và trả về GameMove
                Future<GameMove> future = executorService.submit(() ->
                        aiGameService.playGame(gameMove.getRow(), gameMove.getCol())
                );

                // Chờ kết quả từ Callable
                GameMove move = future.get();

                aiGameService.setPlayerTurn(true);
                if (move.isWin() || move.getRow() == -1) {
                    gameRoomManager.remove(roomCode);
                }

                return ResponseEntity.ok(move);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error processing move: " + e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Not your turn");
    }

    @PostMapping("/leave")
    public boolean leave(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestParam String roomCode) {
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


}