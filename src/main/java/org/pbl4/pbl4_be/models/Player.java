package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

@Setter
@Getter
public class Player implements Principal {
    private String playerId;
    private String playerName;



    public Player() {}

    public Player(String playerId) {
        this.playerId = playerId;

    }

    @Override
    public String getName() {
        return playerId;
    }
}
