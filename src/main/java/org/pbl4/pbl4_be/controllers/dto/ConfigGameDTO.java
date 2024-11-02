package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;
import org.pbl4.pbl4_be.enums.FirstMoveOption;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigGameDTO {
    private String ownerId;
    private int timeLimitForMove;
    private int timeLimitForMatch;
    private FirstMoveOption firstMoveOption;
}
