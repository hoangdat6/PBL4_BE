package org.pbl4.pbl4_be.models;


import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.Math.max;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayerTimeInfo {
    private Long playerId;
    private Integer remainTime; // in seconds
    private Integer remainMoveDuration; // in seconds
    private Integer playedTime; // in seconds
    private LocalDateTime lastUpdateTime;

    public void setTimeInfo(Integer moveDuration, LocalDateTime startTimeMove){
        int Time1 = moveDuration - (int) Duration.between(startTimeMove, LocalDateTime.now()).getSeconds(), Time2 = 0;
        this.remainMoveDuration = max(0, Time1);
        if(remainMoveDuration == 0){
            Time2 = (int) Duration.between(lastUpdateTime, LocalDateTime.now()).getSeconds() + Time1;
            this.remainTime = max(0, remainTime - Time2);
        }else {
            Time2 = (int) Duration.between(lastUpdateTime, LocalDateTime.now()).getSeconds();
            this.remainTime = max(0, remainTime - Time2);
        }
        this.playedTime += Time2;
        this.lastUpdateTime = LocalDateTime.now();
    }
}