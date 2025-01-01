package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinAIRoomResponse {
    private String roomCode;
    private Long playerId;
    private String playerName;
    private String playerAvatar;
    private Integer playerScore;
}
