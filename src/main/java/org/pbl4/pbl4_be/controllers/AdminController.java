package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.models.Season;
import org.pbl4.pbl4_be.services.AdminService;
import org.pbl4.pbl4_be.services.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final SeasonService seasonService;

    public AdminController(AdminService adminService, SeasonService seasonService) {
        this.adminService = adminService;
        this.seasonService = seasonService;
    }

    @GetMapping("/get-all-players")
    public ResponseEntity<?> getAllPlayers(@RequestParam String sort, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(adminService.getAllPlayers(sort, page, size));
    }

    @GetMapping("/match-list")
    public ResponseEntity<?> getAllCurrentMatches() {
        return ResponseEntity.ok(adminService.getAllCurrentMatches());
    }

    @GetMapping("/season-statistic")
    public ResponseEntity<?> getSeasonStatistic() {
        Season season = seasonService.findCurrentSeason().orElse(null);
        return ResponseEntity.ok(adminService.getSeasonStatistic(season));
    }
}
