package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Getter
@Setter
public class PlayerStatisticDTO {
    private Long id;
    private String name;
    private String email;
    private int totalGame;
    private int winGame;
    private int loseGame;
    private int drawGame;
    private Timestamp createdAt;

    public PlayerStatisticDTO(Long id, String name, String email, Timestamp createdAt, int totalGame, int winGame, int loseGame, int drawGame) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.totalGame = totalGame;
        this.winGame = winGame;
        this.loseGame = loseGame;
        this.drawGame = drawGame;
    }

}
