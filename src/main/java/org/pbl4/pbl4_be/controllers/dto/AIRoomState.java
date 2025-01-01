package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;
import org.pbl4.pbl4_be.models.Board;
import org.pbl4.pbl4_be.models.GameMove;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIRoomState {
    private String roomCode;
    private GameMove lastMove;
    private boolean isPlayerTurn;
    private BoardDTO board;
    private short nthMove;
    private Long playerId;
    private String playerName;
    private String playerAvatar;
    private Integer playerScore;
}
