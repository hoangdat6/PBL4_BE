package org.pbl4.pbl4_be.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.models.PlayerMatching;

@Getter
@Setter
public class PlayerMatchingResponse {
    private PlayerMatching player1;
    private PlayerMatching player2;
    private String roomCode;
    
    public PlayerMatchingResponse(PlayerMatching player1, PlayerMatching player2, String roomCode) {
        this.player1 = player1;
        this.player2 = player2;
        this.roomCode = roomCode;
    }
}
