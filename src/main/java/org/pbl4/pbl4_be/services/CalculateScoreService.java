package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.models.*;
import org.springframework.stereotype.Service;

@Service
public class CalculateScoreService {
    private final SeasonService seasonService;
    private final PlayerSeasonService playerSeasonService;
    private final UserService userService;
    public CalculateScoreService(SeasonService seasonService, PlayerSeasonService playerSeasonService, UserService userService) {
        this.seasonService = seasonService;
        this.playerSeasonService = playerSeasonService;
        this.userService = userService;
    }

    public void updateScore(Room room, boolean isDraw) {
        Game game = room.getLastGame();
        Season season = seasonService.findCurrentSeason().orElse(null);
        if (season != null) {
            PlayerSeason playerSeason1 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getFirstPlayerId()).orElse(new PlayerSeason(userService.findById(game.getFirstPlayerId()).orElse(null), season));
            PlayerSeason playerSeason2 = playerSeasonService.findBySeasonIdAndPlayerId(season.getId(), game.getSecondPlayerId()).orElse(new PlayerSeason(userService.findById(game.getSecondPlayerId()).orElse(null), season));
            if(isDraw){
                playerSeason1.updateScore(false, true);
                playerSeason2.updateScore(false, true);
            }else{
                playerSeason1.updateScore(game.getFirstPlayerId().equals(game.getWinnerId()), false);
                playerSeason2.updateScore(game.getSecondPlayerId().equals(game.getWinnerId()), false);
                playerSeason1.increasePlayerTime(room.getGameConfig().getTotalTime() - game.getFirstPlayerInfo().getRemainTime());
                playerSeason2.increasePlayerTime(room.getGameConfig().getTotalTime() - game.getSecondPlayerInfo().getRemainTime());
                if (playerSeason1.getWinStreak() != 0) {
                    playerSeason1.bonusScoreTime(game.getFirstPlayerInfo().getRemainTime(), game.getSecondPlayerInfo().getRemainTime());
                } else if (playerSeason2.getWinStreak() != 0) {
                    playerSeason2.bonusScoreTime(game.getSecondPlayerInfo().getRemainTime(), game.getFirstPlayerInfo().getRemainTime());
                }
            }
            room.updateSeasonScore(playerSeason1);
            room.updateSeasonScore(playerSeason2);
            playerSeasonService.save(playerSeason1);
            playerSeasonService.save(playerSeason2);
        }
    }
}
