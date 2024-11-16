package org.pbl4.pbl4_be.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.enums.ParticipantType;

@Getter
@Setter
@Builder
public class JoinRoomResponse {
    private String roomCode;
    private ParticipantType participantType;
    private Boolean isStarted;
}