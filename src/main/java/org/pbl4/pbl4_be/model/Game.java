package org.pbl4.pbl4_be.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private static short moveCount = 0;
    private Integer gameId;
    private String roomId;
    private short[][] board;
    private String firstPlayerId;
    private Boolean isWin;

    public Game(String roomId, Integer gameId, String firstPlayerId) {
        this.roomId = roomId;
        this.gameId = gameId;
        this.board = new short[16][16];
        this.firstPlayerId = firstPlayerId;
        this.isWin = null;
    }

    public void increaseMoveCnt() {
        ++moveCount;
    }

    public boolean processMove(GameMove move) {
        // Xử lý nước đi của người chơi

        board[move.getRow()][move.getCol()] = (short) (move.getNthMove() % 2);
        return checkWin(move.getRow(), move.getCol(), (short) (move.getNthMove() % 2));
    }

    public boolean checkWin(int row, int col, short symbol) {
        return checkRow(row, col, symbol) || checkCol(row, col, symbol) || checkDiagonal(row, col, symbol);
    }

    public boolean checkRow(int row, int col, short symbol) {
        // Kiểm tra hàng ngang
        int cnt = 1;
        int i = col - 1;
        while (i >= 0 && board[row][i] == symbol) {
            cnt++;
            i--;
        }
        i = col + 1;
        while (i < 16 && board[row][i] == symbol) {
            cnt++;
            i++;
        }
        return cnt >= 5;
    }

    public boolean checkCol(int row, int col, short symbol) {
        // Kiểm tra hàng dọc
        int cnt = 1;
        int i = row - 1;
        while (i >= 0 && board[i][col] == symbol) {
            cnt++;
            i--;
        }
        i = row + 1;
        while (i < 16 && board[i][col] == symbol) {
            cnt++;
            i++;
        }
        return cnt >= 5;
    }

    public boolean checkDiagonal(int row, int col, short symbol) {
        // Kiểm tra chéo chính
        int cnt = 1;
        int i = row - 1;
        int j = col - 1;
        while (i >= 0 && j >= 0 && board[i][j] == symbol) {
            cnt++;
            i--;
            j--;
        }
        i = row + 1;
        j = col + 1;
        while (i < 16 && j < 16 && board[i][j] == symbol) {
            cnt++;
            i++;
            j++;
        }
        if (cnt >= 5) {
            return true;
        }
        // Kiểm tra chéo phụ
        cnt = 1;
        i = row - 1;
        j = col + 1;
        while (i >= 0 && j < 16 && board[i][j] == symbol) {
            cnt++;
            i--;
            j++;
        }
        i = row + 1;
        j = col - 1;
        while (i < 16 && j >= 0 && board[i][j] == symbol) {
            cnt++;
            i++;
            j--;
        }
        return cnt >= 5;
    }

    // Các phương thức xử lý logic khác (xử lý nước đi, kiểm tra thắng/thua...)
}