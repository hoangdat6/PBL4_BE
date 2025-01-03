package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.models.User;

import java.time.ZonedDateTime;

@Getter
@Setter
public class HistoryDTO {
    private Long roomId;
    private PlayerHistoryDTO player1;
    private PlayerHistoryDTO player2;
    private ZonedDateTime createdAt;

    public HistoryDTO(RoomDTO room, User player1, User player2) {
        this.roomId = room.getId();
        this.player1 = new PlayerHistoryDTO(player1);
        this.player2 = new PlayerHistoryDTO(player2);
        this.createdAt = room.getCreatedAt();
    }

    public void UpdateScore(Long winnerId) {
        if (player1.getId() == winnerId) {
            player1.setScore(player1.getScore() + 1);
        } else if(player2.getId() == winnerId){
            player2.setScore(player2.getScore() + 1);
        } else {
            player1.setScore(player1.getScore() + 1);
            player2.setScore(player2.getScore() + 1);
        }
    }
}
