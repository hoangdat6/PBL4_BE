package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private Long id;
    private String name;
    private String avatar;
    private Integer maxRating;
    private Integer rank;
    private Integer points;
    private Integer wins;
    private Integer draws;
    private Integer losses;
    private Integer streaks;
    private String playTimes;
    private String dateJoined;
}