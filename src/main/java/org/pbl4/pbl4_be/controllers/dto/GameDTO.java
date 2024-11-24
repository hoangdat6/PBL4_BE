package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GameDTO {
    private Long id;
    private Long winnerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long firstPlayerId;
    private LocalDateTime createdAt;
    private List<GameMoveDTO> moves;  // Danh sách các nước đi

    public GameDTO(Long id, Long winnerId, LocalDateTime startTime, LocalDateTime endTime, Long firstPlayerId, LocalDateTime createdAt, List<GameMoveDTO> moves) {
        this.id = id;
        this.winnerId = winnerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.firstPlayerId = firstPlayerId;
        this.createdAt = createdAt;
        this.moves = moves;
    }
}
