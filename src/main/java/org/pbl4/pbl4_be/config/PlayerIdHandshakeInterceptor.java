package org.pbl4.pbl4_be.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

public class PlayerIdHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Lấy playerId từ headers
//        String playerId = request.getHeaders().getFirst("playerId");

        // Lấy roomCode từ query parameters trong URL
        URI uri = request.getURI();
        String query = uri.getQuery(); // Lấy query string từ URL (vd: "roomCode=123456")

        if (query != null) {
            // Tìm kiếm "roomCode" trong query string
            String[] queryParams = query.split("&");
            String roomCode = null;
            for (String param : queryParams) {
                if (param.startsWith("roomCode=")) {
                    roomCode = param.split("=")[1];
                    break;
                }
            }

            if (roomCode != null) {
                attributes.put("roomCode", roomCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
// Lấy roomCode từ attributes
//        Map<String, Object> attributes = (Map<String, Object>) request.get();
//        String roomCode = (String) attributes.get("roomCode");
//
//        if (roomCode != null) {
//            System.out.println("Room code after handshake: " + roomCode);
//        }

    }
}
