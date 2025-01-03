package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class RoomDTO {
    private Long id;
    private String code;
    private Long player1Id;
    private Long player2Id;
    private Integer gameDuration;
    private Integer moveDuration;
    private Boolean isPrivate;
    private String status;
    private Long createdBy;
    private ZonedDateTime createdAt;
    private List<GameDTO> games;

    public RoomDTO(Long id, String code, Long player1Id, Long player2Id, Integer gameDuration, Integer moveDuration, Boolean isPrivate, String status, Long createdBy, ZonedDateTime createdAt, List<GameDTO> games) {
        this.id = id;
        this.code = code;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gameDuration = gameDuration;
        this.moveDuration = moveDuration;
        this.isPrivate = isPrivate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.games = games;
    }
}
