package org.pbl4.pbl4_be.controller.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GameStartDTO {
    private String roomCode;
    private String startPlayerId;
}
