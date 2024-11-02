package org.pbl4.pbl4_be.controllers.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private String roomCode;
    private String ownerId;

}
