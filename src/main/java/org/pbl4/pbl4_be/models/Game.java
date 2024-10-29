package org.pbl4.pbl4_be.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private Integer gameId;
    private String roomId;
    private Board board;
    private String firstPlayerId;
    private Boolean isWin;
    private short nthMove;

    public Game(String roomId, Integer gameId, String firstPlayerId) {
        this.roomId = roomId;
        this.gameId = gameId;
        this.board = new Board(16, 5);
        this.firstPlayerId = firstPlayerId;
        this.isWin = null;
        this.nthMove = 0;
    }

    public void increaseMoveCnt() {
        this.nthMove++;
    }

    public boolean  processMove(GameMove move) {
        // Xử lý nước đi của người chơi
        board.setMove(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
        return board.checkWin(move.getRow(), move.getCol(), (byte) (move.getNthMove() % 2));
    }

}