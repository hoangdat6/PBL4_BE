package org.pbl4.pbl4_be.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerTimeInfo {
    private Long playerId;
    private Integer remainTime; // in seconds
    private Integer remainMoveDuration; // in seconds
    private Integer playedTime; // in seconds
}