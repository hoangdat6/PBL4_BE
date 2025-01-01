package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private byte[][] board;
    private int size;
    private int winLength;

    public Board(byte[][] board, int size, int winLength) {
        this.board = board;
        this.size = size;
        this.winLength = winLength;
    }

    public Board(int size, int winLength) {
        this.size = size;
        this.winLength = winLength;
        this.board = new byte[size][size];
        reset();
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = -1;
            }
        }
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == -1) {
                    return false;
                }
            }
        }

        return true;
    }

    public void setMove(int row, int col, byte symbol) {
        board[row][col] = symbol;
    }

    public boolean checkWin(int row, int col, byte symbol) {
        return checkRow(row, col, symbol) || checkCol(row, col, symbol) || checkDiagonal(row, col, symbol);
    }

    public boolean checkRow(int row, int col, byte symbol) {
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
        return cnt >= winLength;
    }

    public boolean checkCol(int row, int col, byte symbol) {
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
        return cnt >= winLength;
    }

    public boolean checkDiagonal(int row, int col, byte symbol) {
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
        if (cnt >= winLength) {
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
        return cnt >= winLength;
    }
}
