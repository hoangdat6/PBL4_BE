package org.pbl4.pbl4_be.ws.services;

import org.pbl4.pbl4_be.controllers.dto.GameState;
import org.pbl4.pbl4_be.models.Player;
import org.pbl4.pbl4_be.models.Room;
import org.pbl4.pbl4_be.services.GameRoomManager;
import org.pbl4.pbl4_be.ws.config.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.pbl4.pbl4_be.Constants.GAME_END_TOPIC;

@Service
public class WSService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WSService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendGameEndMessage(String roomCode, Object message) {
        messagingTemplate.convertAndSend(GAME_END_TOPIC + roomCode, ResponseEntity.ok(message));
    }

    public void sendToUser(Long userId, String destination, Object object) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), destination, object);
    }

    public void sendAllPlayers(List<Player> players, String destination, Object object) {
        messagingTemplate.convertAndSendToUser(String.valueOf(players.get(1).getId()), destination, object);
        messagingTemplate.convertAndSendToUser(String.valueOf(players.get(0).getId()), destination, object);
    }

    public void sendAllSpecs(List<Player> specs, String destination, Object object) {
        for (Player spec : specs) {
            sendToUser(spec.getId(), destination, object);
        }
    }

    public void sendAllForRoom(Room room, String destination, Object object) {
        if (room == null) {
            return;
        }
        messagingTemplate.convertAndSend(destination, object);
    }
}
