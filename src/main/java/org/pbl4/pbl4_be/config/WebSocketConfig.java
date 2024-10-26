package org.pbl4.pbl4_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cấu hình endpoint WebSocket và cho phép kết nối từ localhost:3000
        registry.addEndpoint("/caro-game")
                .setAllowedOrigins("http://localhost:3000") // Cho phép CORS từ localhost:3000
//                .setHandshakeHandler(new CustomHandshakeHandler()) // Sử dụng handshake handler
//                .addInterceptors(new PlayerIdHandshakeInterceptor()) // Thêm interceptor
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }


}
