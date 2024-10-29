package org.pbl4.pbl4_be.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.pbl4.pbl4_be.models.User;
import org.pbl4.pbl4_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest, HttpServletResponse response) {
//        Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());
//        if (userOptional.isPresent()
////                && passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())
//        ) {
//            Cookie cookie = new Cookie("user", userOptional.get().getEmail());
//            cookie.setHttpOnly(true);
//            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
//            response.addCookie(cookie);
//            return ResponseEntity.ok(userOptional.get());
//        } else {
//            return ResponseEntity.status(401).body("Invalid email or password");
//        }
        return ResponseEntity.ok(loginRequest.getEmail());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("user", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }
}
