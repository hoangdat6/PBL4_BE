package org.pbl4.pbl4_be.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MatchData {
    private String date;
    private int matches;
    public MatchData(String date, int matches) {
        this.date = date;
        this.matches = matches;
    }
}
