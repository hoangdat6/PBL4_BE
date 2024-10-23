package org.pbl4.pbl4_be.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private String roomCode;
    private String ownerId;
    private int playerCount;
}
