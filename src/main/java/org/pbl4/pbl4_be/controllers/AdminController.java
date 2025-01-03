package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/get-all-players")
    public ResponseEntity<?> getAllPlayers(@RequestParam String sort, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(adminService.getAllPlayers(sort, page, size));
    }

    @GetMapping("/match-list")
    public ResponseEntity<?> getAllCurrentMatches() {
        return ResponseEntity.ok(adminService.getAllCurrentMatches());
    }


}
