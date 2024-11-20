package org.pbl4.pbl4_be.models;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Getter
@Setter
public class  Game {
    private Integer gameId;
    private String roomId;
    private Board board;
    private Long firstPlayerId;
    private Long secondPlayerId;
    private short nthMove;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private Long winnerId;
    private List<GameMove> moveList;
    private GameStatus gameStatus;
    private LocalDateTime time;
    private boolean isPlayAgain;

    public Game(String roomId, Integer gameId) {
        this.roomId = roomId;
        this.gameId = gameId;
        this.board = new Board(16, 5);
        this.nthMove = 0;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.winnerId = null;
        this.moveList = new ArrayList<>();
        this.gameStatus = GameStatus.NOT_STARTED;
        this.createdAt = LocalDateTime.now();
        this.isPlayAgain = false;
    }

    public void increaseMoveCnt() {
        this.nthMove++;
    }

    public boolean  processMove(GameMove move) {
        // Xử lý nước đi của người chơi
        board.setMove(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
        setTime();
        return board.checkWin(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
    }

    public void setTime(){
        this.time = LocalDateTime.now();
    }

    public boolean isEnd() {
        return gameStatus == GameStatus.ENDED;
    }

    public void setFirstAndSecondPlayerId(Map.Entry<Long, Long> players) {
        this.firstPlayerId = players.getKey();
        this.secondPlayerId = players.getValue();
    }

    public void startGame() {
        this.time = LocalDateTime.now();
        this.gameStatus = GameStatus.STARTED;
    }


    public GameMove getLastMove() {
        if (moveList.isEmpty()) {
            return null;
        }
        return moveList.get(moveList.size() - 1);
    }
}