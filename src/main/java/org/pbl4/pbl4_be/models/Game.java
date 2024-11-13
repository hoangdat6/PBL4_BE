package org.pbl4.pbl4_be.models;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.GameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Getter
@Setter
public class Game {
    private Integer gameId;
    private String roomId;
    private Board board;
    private String firstPlayerId;
    private String secondPlayerId;
    private short nthMove;
    private TimeZone startTime;
    private TimeZone endTime;
    private String winnerId;
    private List<GameMove> moveList;
    private GameStatus gameStatus;


    public Game(String roomId, Integer gameId, Map.Entry<String, String> players) {
        this.roomId = roomId;
        this.gameId = gameId;
        this.board = new Board(16, 5);
        this.firstPlayerId = players.getKey();
        this.secondPlayerId = players.getValue();
        this.nthMove = 0;
        this.startTime = TimeZone.getTimeZone("UTC");
        this.endTime = TimeZone.getTimeZone("UTC");
        this.winnerId = null;
        this.moveList = new ArrayList<>();
        this.gameStatus = GameStatus.STARTED;
    }

    public void increaseMoveCnt() {
        this.nthMove++;
    }

    public boolean  processMove(GameMove move) {
        // Xử lý nước đi của người chơi
        board.setMove(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
        return board.checkWin(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
    }

    public boolean isEnd() {
        return gameStatus == GameStatus.ENDED;
    }
}