package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.controllers.dto.LeaderboardDTO;
import org.pbl4.pbl4_be.models.PlayerSeason;
import org.pbl4.pbl4_be.payload.response.LeaderboardResponse;
import org.pbl4.pbl4_be.repositories.PlayerSeasonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class GameService {
    private final PlayerSeasonRepository playerSeasonRepository;
    private final SeasonService seasonService;

    public GameService(PlayerSeasonRepository playerSeasonRepository, SeasonService seasonService) {
        this.playerSeasonRepository = playerSeasonRepository;
        this.seasonService = seasonService;
    }

    public LeaderboardResponse getLeaderboard(String rankings, int pageNumber, int pageSize) {
        Long seasonId = seasonService.findCurrentSeason().isPresent() ? seasonService.findCurrentSeason().get().getId() : null;
        if (seasonId == null) {
            return new LeaderboardResponse(new ArrayList<>(), 0, 0);
        }
        int lowScore = 0;
        int highScore = 1000000;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "score"));
        switch (rankings) {
            case "Rookie":
                highScore = 100;
                break;
            case "Professional":
                lowScore = 101;
                highScore = 300;
                break;
            case "Master":
                lowScore = 301;
                pageable = PageRequest.of(pageNumber + 2, pageSize, Sort.by(Sort.Direction.DESC, "score"));
                break;
            case "Challenger":
                lowScore = 301;
                if (pageNumber > 1) {
                    pageNumber = 1;  // Đảm bảo rằng chỉ có 2 trang (0 và 1) cho Challenger
                }
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "score"));
                break;
            default:
                break;
        }

        Page<PlayerSeason> playerSeasons = playerSeasonRepository
                .getLeaderboard(seasonId, lowScore, highScore, pageable);

        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardDTO> leaderboardDTOs = playerSeasons.stream()
                .map(playerSeason -> new LeaderboardDTO(
                        rank.getAndIncrement(),
                        playerSeason.getPlayer().getAvatar(),
                        playerSeason.getPlayer().getName(),
                        playerSeason.getWinCount(),
                        playerSeason.getLoseCount(),
                        playerSeason.getDrawCount(),
                        playerSeason.getWinStreak(),
                        formatPlayTime(playerSeason.getPlayerTime()),
                        playerSeason.getScore()
                ))
                .collect(Collectors.toList());
        int totalPage = playerSeasons.getTotalPages();
        if(rankings.equals("Master")){
            totalPage = max(0, totalPage - 2);
        }else if(rankings.equals("Challenger")){
            totalPage = min(2, totalPage);
        }
        return new LeaderboardResponse(
                leaderboardDTOs,
                totalPage,
                pageNumber
        );
    }
    private String formatPlayTime(Integer playTimeInSeconds) {
        if(playTimeInSeconds == null){
            return "0 giờ 0 phút";
        }
        int hours = playTimeInSeconds / 3600;
        int minutes = (playTimeInSeconds % 3600) / 60;
        return String.format("%d giờ %d phút", hours, minutes);
    }
}
