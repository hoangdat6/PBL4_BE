package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchDTO {
    private Long roomCode;
    PlayerMatchDTO player1;
    PlayerMatchDTO player2;
    public MatchDTO(Long roomCode, PlayerMatchDTO player1, PlayerMatchDTO player2) {
        this.roomCode = roomCode;
        this.player1 = player1;
        this.player2 = player2;
    }
}
