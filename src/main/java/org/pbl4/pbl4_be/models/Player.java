package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Principal;

@Setter
@Getter
@NoArgsConstructor
public class Player {
    private String playerId;
    private String playerName;
    private boolean isLeaveRoom;

    public Player(String playerId) {
        this.playerId = playerId;

    }

}
