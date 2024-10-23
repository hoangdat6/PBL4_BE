package org.pbl4.pbl4_be.enums;

public enum RoomStatusTypes {
    GAME_NOT_STARTED(0),
    GAME_STARTED(1),
    GAME_ENDED(2);

    private final int value;

    RoomStatusTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
