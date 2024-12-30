package org.pbl4.pbl4_be.payload.response;

import lombok.Getter;
import lombok.Setter;
import org.pbl4.pbl4_be.controllers.dto.LeaderboardDTO;

import java.util.List;

@Getter
@Setter
public class LeaderboardResponse {
    private List<LeaderboardDTO> leaderboard; // Danh sách người chơi
    private int totalPages;                    // Tổng số trang
    private int currentPage;                   // Trang hiện tại

    // Constructor
    public LeaderboardResponse(List<LeaderboardDTO> leaderboard, int totalPages, int currentPage) {
        this.leaderboard = leaderboard;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

}

