package org.pbl4.pbl4_be.models;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Player {
    private Long id;
    private String name;
    private String avatar;
    private byte score;
    private int rank;
    private boolean isLeaveRoom;
    private boolean isReady;

    public void increaseScore() {
        score++;
    }
}
