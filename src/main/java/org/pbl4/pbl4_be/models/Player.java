package org.pbl4.pbl4_be.models;

import lombok.*;

import java.security.Principal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    private Long playerId;
    private String email;
    private String playerName;
    private boolean isLeaveRoom;

    public Player(Long playerId) {
        this.playerId = playerId;

    }

}
