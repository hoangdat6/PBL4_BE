package org.pbl4.pbl4_be.controllers.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.models.*;

import java.util.List;

@Getter
@Setter
@Builder
public class GameState {
    private String roomCode;
    private Long startPlayerId;
    private Long winnerId;
    private short nthMove;
    private GameMove lastMove;
    private BoardDTO boardState;
    private GameConfig gameConfig;
    private PlayerForGameState player1Info;
    private PlayerForGameState player2Info;
    private List<Player> spectators;
    private List<Message> messages;

    public void setBoardState(Board board) {
        Gson gson = new Gson();
        this.boardState = BoardDTO.builder()
                .board(gson.toJson(board.getBoard()))
                .size(board.getSize())
                .winLength(board.getWinLength())
                .build();
    }
}
