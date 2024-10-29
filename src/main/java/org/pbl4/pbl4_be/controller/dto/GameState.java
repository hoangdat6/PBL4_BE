package org.pbl4.pbl4_be.controller.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.models.Board;

@Getter
@Setter
@Builder
public class GameState {
    private String roomCode;
    private String startPlayerId;
    private short nthMove;
    private BoardDTO boardState;

    public void setBoardState(Board board) {
        Gson gson = new Gson();
        this.boardState = BoardDTO.builder()
                .board(gson.toJson(board.getBoard()))
                .size(board.getSize())
                .winLength(board.getWinLength())
                .build();
    }
}
