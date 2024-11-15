package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;

@Getter
@Setter
public class GameMove {
    private int row;
    private int col;
    private short nthMove;
    private boolean isWin;
    private Duration duration;

    public GameMove() {
        // No-argument constructor
    }

    public GameMove(int row, int col, short nthMove) {
        this.row = row;
        this.col = col;
        this.nthMove = nthMove;
        this.isWin = false;
    }

}
