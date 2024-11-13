package org.pbl4.pbl4_be.ws.controller;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayAgainResponse {
    private String code;
    private String roomCode;
    private String playerId;
    private String message;
}
