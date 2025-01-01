package org.pbl4.pbl4_be.controllers.dto;

import com.google.gson.Gson;
import lombok.*;
import org.pbl4.pbl4_be.models.Board;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private String board;
    private int size;
    private int winLength;

    public BoardDTO(Board board) {
        Gson gson = new Gson();
        this.board = gson.toJson(board.getBoard());
        this.size = board.getSize();
        this.winLength = board.getWinLength();
    }
}
