package org.pbl4.pbl4_be.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JoinRequest {
    private String roomId;
    private String playerName;
    private String sessionId;

    public JoinRequest(String roomId, String playerName, String sessionId) {
        this.roomId = roomId;
        this.playerName = playerName;
        this.sessionId = sessionId;
    }

    // Getters và setters
}

