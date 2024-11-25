package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.PlayerSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerSeasonRepository extends JpaRepository<PlayerSeason, Long>{
    @Query("SELECT ps FROM PlayerSeason ps WHERE ps.season.id = :seasonId AND ps.player.id = :playerId")
    Optional<PlayerSeason> findBySeasonIdAndPlayerId(Long seasonId, Long playerId);
}
