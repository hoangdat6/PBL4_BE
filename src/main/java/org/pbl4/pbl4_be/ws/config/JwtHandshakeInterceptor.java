//package org.pbl4.pbl4_be.ws.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.NonNull;
//import org.pbl4.pbl4_be.security.jwt.JwtUtils;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.util.Map;
//
//public class JwtHandshakeInterceptor implements HandshakeInterceptor {
//
//    private final JwtUtils jwtUtils; // Tiện ích xử lý JWT
//
//    public JwtHandshakeInterceptor(JwtUtils jwtUtils) {
//        this.jwtUtils = jwtUtils;
//    }
//
//    @Override
//    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
//                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
//        if (request instanceof ServletServerHttpRequest) {
//            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//            String jwt = jwtUtils.getJwtFromCookies(servletRequest); // Lấy JWT từ Cookie hoặc Header
//
//            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                String email = jwtUtils.getEmailFromJwtToken(jwt); // Giải mã thông tin người dùng từ JWT
//                attributes.put("email", email); // Lưu userId để sử dụng trong WebSocket
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception exception) {
//    }
//}
