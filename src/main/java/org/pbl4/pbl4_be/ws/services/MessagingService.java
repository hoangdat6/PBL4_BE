package org.pbl4.pbl4_be.ws.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static org.pbl4.pbl4_be.Constants.END_GAME_URL;

@Service
public class MessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendGameEndMessage(String roomCode, Object message) {
        messagingTemplate.convertAndSend( END_GAME_URL + roomCode, ResponseEntity.ok(message));
    }

    public void sendGameStateMessage(String roomCode, Object message) {
        messagingTemplate.convertAndSend("/topic/game-state/" + roomCode, ResponseEntity.ok(message));
    }

}
