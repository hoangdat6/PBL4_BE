package org.pbl4.pbl4_be.ws.controllers;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayAgainResponse {
    private String code;
    private String roomCode;
    private Long playerId;
    private String message;
}
