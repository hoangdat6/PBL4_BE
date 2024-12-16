package org.pbl4.pbl4_be.controllers.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAlreadyInRoomException extends RuntimeException {
    private HttpStatus code;
    private String message;
    private String roomCode;

    public PlayerAlreadyInRoomException(String message, String roomCode) {
        this.message = message;
        this.roomCode = roomCode;
    }
}