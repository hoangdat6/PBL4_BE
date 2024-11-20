package org.pbl4.pbl4_be.controllers.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.ParticipantType;
import org.pbl4.pbl4_be.models.Board;
import org.pbl4.pbl4_be.models.GameMove;

@Getter
@Setter
@Builder
public class GameState {
    private String roomCode;
    private Long startPlayerId;
    private short nthMove;
    private GameMove lastMove;
    private BoardDTO boardState;
    private Long player1Id;
    private Long player2Id;

    public void setBoardState(Board board) {
        Gson gson = new Gson();
        this.boardState = BoardDTO.builder()
                .board(gson.toJson(board.getBoard()))
                .size(board.getSize())
                .winLength(board.getWinLength())
                .build();
    }
}
