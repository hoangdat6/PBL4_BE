package org.pbl4.pbl4_be.controllers.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardDTO {
    private String board;
    private int size;
    private int winLength;
}
