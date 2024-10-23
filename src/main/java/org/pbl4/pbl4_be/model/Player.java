package org.pbl4.pbl4_be.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player {
    private String playerId;
    private String playerName;



    public Player() {}

    public Player(String playerId) {
        this.playerId = playerId;

    }

}
