package org.pbl4.pbl4_be.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private String name;
    private String avatar;
    private String email;
    private Integer maxRating;
    private Integer lastSeason;
    private LocalDateTime lastLogin;
}
