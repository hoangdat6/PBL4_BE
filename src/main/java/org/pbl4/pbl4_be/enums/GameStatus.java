package org.pbl4.pbl4_be.enums;

import lombok.Getter;

@Getter
public enum GameStatus {
    NOT_STARTED(0),
    STARTED(1),
    ENDED(2),
    PENDING(3);

    private final int value;

    GameStatus(int value) {
        this.value = value;
    }

}
