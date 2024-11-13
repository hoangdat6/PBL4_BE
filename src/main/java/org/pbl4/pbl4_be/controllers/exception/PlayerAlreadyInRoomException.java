package org.pbl4.pbl4_be.controllers.exception;

public class PlayerAlreadyInRoomException extends RuntimeException {
    public PlayerAlreadyInRoomException(String message) {
        super(message);
    }
}