package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.controllers.dto.GameDTO;
import org.pbl4.pbl4_be.controllers.dto.HistoryDTO;
import org.pbl4.pbl4_be.controllers.dto.RoomDTO;
import org.pbl4.pbl4_be.controllers.dto.UserDTO;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.PlayerSeasonService;
import org.pbl4.pbl4_be.services.RoomDBService;
import org.pbl4.pbl4_be.services.SeasonService;
import org.pbl4.pbl4_be.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    final RoomDBService roomDBService;
    final UserService userService;
    final SeasonService seasonService;
    final PlayerSeasonService playerSeasonService;

    public UserController(RoomDBService roomDBService, UserService userService, SeasonService seasonService, PlayerSeasonService playerSeasonService) {
        this.roomDBService = roomDBService;
        this.userService = userService;
        this.seasonService = seasonService;
        this.playerSeasonService = playerSeasonService;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = currentUser.getId();
        System.out.println(currentUser.getName());
        List<RoomDTO> list = roomDBService.getHistory(userId);
        List<HistoryDTO> result = new ArrayList<>();
        for(RoomDTO roomDTO : list){
            User player1 = userService.findById(roomDTO.getPlayer1Id()).orElseThrow(() -> new BadRequestException("User not found"));
            User player2 = userService.findById(roomDTO.getPlayer2Id()).orElseThrow(() -> new BadRequestException("User not found"));
            HistoryDTO historyDTO = new HistoryDTO(roomDTO, player1, player2);
            for(GameDTO gameDTO : roomDTO.getGames()){
                historyDTO.UpdateScore(gameDTO.getWinnerId());
            }
            result.add(historyDTO);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(userService.findProfileById(currentUser.getId()).orElse(null));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long id = currentUser.getId();
        Long seasonId = seasonService.findCurrentSeason().orElseThrow(() -> new BadRequestException("Season not found")).getId();
        Integer score = playerSeasonService.findBySeasonIdAndPlayerId(seasonId, id).isPresent() ? playerSeasonService.findBySeasonIdAndPlayerId(seasonId, id).get().getScore() : null; // null if not in season
        return ResponseEntity.ok(userService.findUserWithScoreBySeasonId(id, score).orElse(null));
    }




}
