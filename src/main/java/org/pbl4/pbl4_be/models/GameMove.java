package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMove {
    private int row;
    private int col;
    private byte nthMove;
    private boolean isWin;

    public GameMove() {
        // No-argument constructor
    }

    public GameMove(int row, int col, byte nthMove) {
        this.row = row;
        this.col = col;
        this.nthMove = nthMove;
        this.isWin = false;
    }

}
