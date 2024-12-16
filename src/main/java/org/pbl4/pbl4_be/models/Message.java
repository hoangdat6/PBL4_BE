package org.pbl4.pbl4_be.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Message {
    private Long id;
    private String message;
    private Long senderId;
    private LocalDateTime sendTime;

    public Message(Long id, String message, Long senderId) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.sendTime = LocalDateTime.now();
    }
}