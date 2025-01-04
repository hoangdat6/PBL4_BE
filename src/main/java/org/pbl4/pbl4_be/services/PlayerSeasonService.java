package org.pbl4.pbl4_be.services;

import org.pbl4.pbl4_be.models.PlayerSeason;
import org.pbl4.pbl4_be.repositories.PlayerSeasonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerSeasonService {
    final PlayerSeasonRepository playerSeasonRepository;

    public PlayerSeasonService(PlayerSeasonRepository playerSeasonRepository) {
        this.playerSeasonRepository = playerSeasonRepository;
    }

    public Optional<PlayerSeason> findBySeasonIdAndPlayerId(Long seasonId, Long playerId) {
        return playerSeasonRepository.findBySeasonIdAndPlayerId(seasonId, playerId);
    }

    public PlayerSeason save(PlayerSeason playerSeason) {
        return playerSeasonRepository.save(playerSeason);
    }

//    public void UpdateScore(Long seasonId, Long playerId, boolean isWin){
//        Optional<PlayerSeason> playerSeason = playerSeasonRepository.findBySeasonIdAndPlayerId(seasonId, playerId);
//        if(playerSeason.isPresent()){
//            PlayerSeason playerSeason1 = playerSeason.get();
//            if(isWin){
//                playerSeason1.increaseScore();
//            }
//            playerSeasonRepository.save(playerSeason1);
//        }else {
//            playerSeason = Optional.of(new PlayerSeason(seasonId, playerId, 1));
//        }
//    }

    public int getRank(int score, Long seasonId) {
        return playerSeasonRepository.countAllByScoreGreaterThan(score, seasonId) + 1;
    }

}
