package org.pbl4.pbl4_be.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDTO {
    private int rank;
    private String avatar;
    private String name;
    private int wins;
    private int losses;
    private int draws;
    private int streak;
    private String playTime;
    private int points;
}
