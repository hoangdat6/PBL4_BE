package org.pbl4.pbl4_be.models;

import lombok.*;
import org.pbl4.pbl4_be.enums.FirstMoveOption;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameConfig {
    private Integer totalTime;
    private Integer moveDuration;
    private FirstMoveOption firstMoveOption;

}
