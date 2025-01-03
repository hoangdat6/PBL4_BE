package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class GameDTO {
    private Long id;
    private Long winnerId;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Long firstPlayerId;
    private ZonedDateTime createdAt;
    private List<GameMoveDTO> moves;  // Danh sách các nước đi

    public GameDTO(Long id, Long winnerId, ZonedDateTime startTime, ZonedDateTime endTime, Long firstPlayerId, ZonedDateTime createdAt, List<GameMoveDTO> moves) {
        this.id = id;
        this.winnerId = winnerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.firstPlayerId = firstPlayerId;
        this.createdAt = createdAt;
        this.moves = moves;
    }
}
