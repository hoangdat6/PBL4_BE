package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMatchDTO {
    private Long id;
    private String name;
    private String avatar;
    private Integer score;
    public PlayerMatchDTO(Long id, String name, String avatar, Integer score) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.score = score;
    }

}
