package org.pbl4.pbl4_be.models;

public class UserRankData {
    private Long userId;
    private int rankScore;

    // Constructors
    public UserRankData(Long userId, int rankScore) {
        this.userId = userId;
        this.rankScore = rankScore;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRankScore() {
        return rankScore;
    }

    public void setRankScore(int rankScore) {
        this.rankScore = rankScore;
    }
}
