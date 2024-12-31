package org.pbl4.pbl4_be.ws.controllers;

import org.pbl4.pbl4_be.enums.FirstMoveOption;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.payload.response.PlayerMatchingResponse;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.services.PlayerSeasonService;
import org.pbl4.pbl4_be.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api/random-match")
public class MatchmakingController {
    private final List<PlayerMatching> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final int MAX_SCORE_DIFFERENCE = 50;
    private static final int MAX_SCORE_SET_TIME = 20;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final PlayerSeasonService playerSeasonService;
    private final SeasonService seasonService;
    private final SimpMessagingTemplate messagingTemplate;
    private int defaultTotalTime = 300;
    private int defaultTotalMove = 40;
    private FirstMoveOption defaultFirstMove = FirstMoveOption.RANDOM;


    @Autowired
    public MatchmakingController(PlayerSeasonService playerSeasonService, SeasonService seasonService, SimpMessagingTemplate messagingTemplate) {
        this.playerSeasonService = playerSeasonService;
        this.seasonService = seasonService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/add-player")
    public ResponseEntity<?> addPlayer(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long seasonId = seasonService.findCurrentSeason().orElseThrow().getId();
        PlayerSeason playerSeason = playerSeasonService.findBySeasonIdAndPlayerId(seasonId, currentUser.getId()).orElseThrow();
        PlayerMatching player = new PlayerMatching(playerSeason.getId(), playerSeason.getScore());
        try {
            CompletableFuture<PlayerMatchingResponse> futureMatch = CompletableFuture.supplyAsync(() -> matchPlayers(player), executorService);
            PlayerMatchingResponse matchedPlayers = futureMatch.join();

            return ResponseEntity.ok(Objects.requireNonNullElse(matchedPlayers, "No match found, added to queue."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing move: " + e.getMessage());
        }
    }

    @PostMapping("/remove-player")
    public ResponseEntity<?> removePlayer(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        lock.lock();
        try {
            Long seasonId = seasonService.findCurrentSeason().orElseThrow().getId();
            PlayerSeason playerSeason = playerSeasonService.findBySeasonIdAndPlayerId(seasonId, currentUser.getId()).orElseThrow();
            PlayerMatching player = new PlayerMatching(playerSeason.getId(), playerSeason.getScore());
            players.remove(player);
        } finally {
            lock.unlock();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get-players")
    public ResponseEntity<?> getPlayers() {
        return ResponseEntity.ok(players);
    }

    private PlayerMatchingResponse matchPlayers(PlayerMatching player) {
        lock.lock();
        try {
            PlayerMatching bestMatch = null;
            int minScoreDifference = Integer.MAX_VALUE;

            for (PlayerMatching otherPlayer : players) {
                if (!Objects.equals(player.getId(), otherPlayer.getId())) {
                    int scoreDifference = Math.abs(player.getScore() - otherPlayer.getScore());
                    if (scoreDifference <= MAX_SCORE_DIFFERENCE) {
                        if (bestMatch == null ||
                                (scoreDifference <= MAX_SCORE_SET_TIME && otherPlayer.getJoinTime() < bestMatch.getJoinTime())) {
                            bestMatch = otherPlayer;
                            minScoreDifference = scoreDifference;
                        } else if (scoreDifference < minScoreDifference) {
                            bestMatch = otherPlayer;
                            minScoreDifference = scoreDifference;
                        }
                    }
                }
            }

            if (bestMatch != null) {
                players.remove(bestMatch);
                String roomCode = randomRoomCode();
                while (GameRoomManager.getInstance().checkRoomExist(roomCode)) {
                    roomCode = randomRoomCode();
                }
                GameConfig gameConfig = new GameConfig(defaultTotalTime, defaultTotalMove, defaultFirstMove);
                GameRoomManager.getInstance().createRoom(roomCode, gameConfig, true);
                sendMatchmakingMessage(new PlayerMatchingResponse(player, bestMatch, roomCode));
                return new PlayerMatchingResponse(player, bestMatch, roomCode);
            }
            players.add(player);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void sendMatchmakingMessage(PlayerMatchingResponse response) {
        try {

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(response.getPlayer1().getId()),
                    "/queue/matchmaking",
                    response
            );
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(response.getPlayer2().getId()),
                    "/queue/matchmaking",
                    response
            );
        } catch (Exception e) {
            // Ghi log lỗi
            System.err.println("Error sending matchmaking message: " + e.getMessage());
        }
    }
    private String randomRoomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
