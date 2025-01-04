package org.pbl4.pbl4_be.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.MatchData;

import java.util.List;

@Getter
@Setter
public class SeasonStatisticResponse {
    private long onlineMatches;
    private long friendMatches;
    private long playersCount;
    private List<MatchData> matchData;
    public SeasonStatisticResponse(long onlineMatches, long friendMatches, long playersCount, List<MatchData> matchData) {
        this.onlineMatches = onlineMatches;
        this.friendMatches = friendMatches;
        this.playersCount = playersCount;
        this.matchData = matchData;
    }
}
