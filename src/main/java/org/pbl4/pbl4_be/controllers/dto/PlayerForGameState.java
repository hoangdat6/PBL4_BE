package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;
import org.pbl4.pbl4_be.models.Player;
import org.pbl4.pbl4_be.models.PlayerTimeInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerForGameState {
    private Long id;
    private String name;
    private String avatar;
    private int matchScore;
    private int seasonScore;
    private int rank;
    private Integer remainTime;
    private Integer remainMoveDuration; // in seconds
    private Integer playedTime;

    public PlayerForGameState(Player player, PlayerTimeInfo timeInfo) {
        this.id = player.getId();
        this.name = player.getName();
        this.avatar = player.getAvatar();
        this.matchScore = player.getMatchScore();
        this.seasonScore = player.getSeasonScore();
        this.rank = player.getRank();
        this.remainTime = timeInfo.getRemainTime();
        this.remainMoveDuration = timeInfo.getRemainMoveDuration();
        this.playedTime = timeInfo.getPlayedTime();
    }
}
