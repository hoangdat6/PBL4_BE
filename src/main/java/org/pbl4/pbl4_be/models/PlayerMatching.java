package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMatching {
    private Long id;
    private int score;
    private long joinTime;

    public PlayerMatching(Long id, int score) {
        this.id = id;
        this.score = score;
        this.joinTime = System.currentTimeMillis();
    }
}
