package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMoveDTO {
    private Long id;
    private String move;
    private Integer duration;

    public GameMoveDTO(Long id, String move, Integer duration) {
        this.id = id;
        this.move = move;
        this.duration = duration;
    }
}
