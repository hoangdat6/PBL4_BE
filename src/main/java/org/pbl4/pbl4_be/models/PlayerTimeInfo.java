package org.pbl4.pbl4_be.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.ZonedDateTime;

import static java.lang.Math.max;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerTimeInfo {
    private Long playerId;
    private Integer remainTime; // in seconds
    private Integer remainMoveDuration; // in secondsSL
    private Integer playedTime; // in seconds
    private ZonedDateTime lastUpdateTime;

    public void setTimeInfo(Integer moveDuration, ZonedDateTime startTimeMove){
        int Time1 = moveDuration - (int) Duration.between(startTimeMove, ZonedDateTime.now()).getSeconds(), Time2 = 0;
        this.remainMoveDuration = max(0, Time1);
        if(remainMoveDuration == 0){
            Time2 = (int) Duration.between(lastUpdateTime, ZonedDateTime.now()).getSeconds() + Time1;
            this.remainTime = max(0, remainTime - Time2);
        }else {
            Time2 = (int) Duration.between(lastUpdateTime, ZonedDateTime.now()).getSeconds();
            this.remainTime = max(0, remainTime - Time2);
        }
        this.playedTime += Time2;
        this.lastUpdateTime = ZonedDateTime.now();
    }

//    public void setTimeInfo(Integer moveDuration, ZonedDateTime startTimeMove){
//        // Tính toán thời gian còn lại trong lượt hiện tại
//        int timePassedInMove = moveDuration - (int) Duration.between(startTimeMove, ZonedDateTime.now()).getSeconds();
//        this.remainMoveDuration = max(0, timePassedInMove);
//
//        // Nếu thời gian lượt di chuyển hết (remainMoveDuration == 0), tính lại thời gian tổng còn lại
//        if (remainMoveDuration == 0) {
//            int timePassedInTotal = (int) Duration.between(lastUpdateTime, ZonedDateTime.now()).getSeconds();
//            this.remainTime = max(0, remainTime - timePassedInTotal);
//        } else {
//            // Nếu lượt di chuyển chưa hết, chỉ tính thời gian đã trôi qua trong lượt này
//            int timePassedInMoveDuringUpdate = (int) Duration.between(lastUpdateTime, ZonedDateTime.now()).getSeconds();
//            this.remainTime = max(0, remainTime - timePassedInMoveDuringUpdate);
//        }
//
//        // Cộng thêm thời gian đã trôi qua vào playedTime
//        this.playedTime += (int) Duration.between(lastUpdateTime, ZonedDateTime.now()).getSeconds();
//
//        // Cập nhật thời gian của lần gọi setTimeInfo
//        this.lastUpdateTime = ZonedDateTime.now();
//    }


    public void setInitialTimeInfo(Integer moveDuration){
        this.remainMoveDuration = moveDuration;
        this.playedTime = 0;
        this.lastUpdateTime = ZonedDateTime.now();
    }
}