package org.pbl4.pbl4_be.models;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.GameStatus;

import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class  Game {
    private Integer gameId;
    private String roomId;
    private Long winnerId;
    private Board board;
    private PlayerTimeInfo firstPlayerInfo; // thông tin người chơi đi nước đầu tiên
    private PlayerTimeInfo secondPlayerInfo; // thông tin người chơi đi nước thứ hai
    private short nthMove;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private ZonedDateTime createdAt;
    private List<GameMove> moveList;
    private GameStatus gameStatus;
    private ZonedDateTime startTimeMove;
    private boolean isPlayAgain;
    private Integer moveDuration;

    public Game(String roomId, Integer gameId, GameConfig gameConfig) {
        this.roomId = roomId;
        this.gameId = gameId;
        this.board = new Board(16, 5);
        this.nthMove = 0;
        this.startTime = ZonedDateTime.now();
        this.endTime = null;
        this.winnerId = null;
        this.moveList = new ArrayList<>();
        this.gameStatus = GameStatus.NOT_STARTED;
        this.createdAt = ZonedDateTime.now();
        this.isPlayAgain = false;
        this.moveDuration = gameConfig.getMoveDuration();
        this.firstPlayerInfo = new PlayerTimeInfo(
                null,
                gameConfig.getTotalTime(),
                gameConfig.getMoveDuration(),
                0,
                ZonedDateTime.now()
        );

        this.secondPlayerInfo = new PlayerTimeInfo(
                null,
                gameConfig.getTotalTime(),
                gameConfig.getMoveDuration(),
                0,
                ZonedDateTime.now()
        );
    }

    public void increaseMoveCnt() {
        this.nthMove++;
    }

    public boolean processMove(GameMove move) {
        // Xử lý nước đi của người chơi
        board.setMove(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
        setStartTimeMove();
        if(move.getNthMove() % 2 == 0){
            firstPlayerInfo.setTimeInfo(moveDuration, startTimeMove);
        }else {
            secondPlayerInfo.setTimeInfo(moveDuration, startTimeMove);
        }
        return board.checkWin(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
    }

    public void setStartTimeMove(){
        this.startTimeMove = ZonedDateTime.now();
    }

    public boolean isEnd() {
        return gameStatus == GameStatus.ENDED;
    }

    public void setFirstAndSecondPlayerId(Map.Entry<Long, Long> players) {
        this.firstPlayerInfo.setPlayerId(players.getKey());
        this.secondPlayerInfo.setPlayerId(players.getValue());
    }

    public void startGame() {
        this.startTimeMove = ZonedDateTime.now();
        this.firstPlayerInfo.setInitialTimeInfo(moveDuration);
        this.secondPlayerInfo.setInitialTimeInfo(moveDuration);
        this.gameStatus = GameStatus.STARTED;
    }

    public GameMove getLastMove() {
        if (moveList.isEmpty()) {
            return null;
        }
        return moveList.get(moveList.size() - 1);
    }

    public Long getFirstPlayerId() {return firstPlayerInfo.getPlayerId();}

    public Long getSecondPlayerId() {
        return secondPlayerInfo.getPlayerId();
    }

    public PlayerTimeInfo getFirstPlayerInfo() {
        if(nthMove % 2 == 0){
            firstPlayerInfo.setTimeInfo(moveDuration, startTimeMove);
        }
        return firstPlayerInfo;
    }

    public PlayerTimeInfo getSecondPlayerInfo() {
        if(nthMove % 2 == 1){
            secondPlayerInfo.setTimeInfo(moveDuration, startTimeMove);
        }
        return secondPlayerInfo;
    }

    public void resetRemainMoveTime(Long playerTurnId) {
        if(playerTurnId.equals(firstPlayerInfo.getPlayerId())){
            this.firstPlayerInfo.setInitialTimeInfo(moveDuration);
        }else {
            this.secondPlayerInfo.setInitialTimeInfo(moveDuration);
        }
    }
}