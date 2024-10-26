package org.pbl4.pbl4_be.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Lấy playerId từ attributes
        String playerId = (String) attributes.get("playerId");
        System.out.println("playerId: " + playerId + " in CustomHandshakeHandler");
        return new StompPrincipal(playerId); // Gán playerId vào Principal
    }
}
