package org.pbl4.pbl4_be.controllers;

import org.pbl4.pbl4_be.controllers.dto.*;
import org.pbl4.pbl4_be.controllers.exception.BadRequestException;
import org.pbl4.pbl4_be.models.*;
import org.pbl4.pbl4_be.services.PlayerSeasonService;
import org.pbl4.pbl4_be.services.RoomDBService;
import org.pbl4.pbl4_be.services.SeasonService;
import org.pbl4.pbl4_be.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    final UserService userService;

    public PlayerController(RoomDBService roomDBService, UserService userService, SeasonService seasonService, PlayerSeasonService playerSeasonService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam Long id) {
        return ResponseEntity.ok(userService.findProfileById(id).orElse(null));
    }
}