package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.models.User;

@Getter
@Setter
public class PlayerHistoryDTO {
    private Long id;
    private String name;
    private Integer score;
    PlayerHistoryDTO(User player) {
        this.id = player.getId();
        this.name = player.getName();
        this.score = 0;
    }
}
