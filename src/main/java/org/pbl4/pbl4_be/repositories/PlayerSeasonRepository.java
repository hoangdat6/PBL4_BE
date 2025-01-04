package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.PlayerSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerSeasonRepository extends JpaRepository<PlayerSeason, Long>{
    @Query("SELECT ps FROM PlayerSeason ps WHERE ps.season.id = :seasonId AND ps.player.id = :playerId")
    Optional<PlayerSeason> findBySeasonIdAndPlayerId(Long seasonId, Long playerId);

    @Query("SELECT ps FROM PlayerSeason ps WHERE ps.season.id = :seasonId AND ps.score >= :lowScore AND ps.score <= :highScore ORDER BY ps.score DESC")
    Page<PlayerSeason> getLeaderboard(
            @Param("seasonId") Long seasonId,
            @Param("lowScore") Integer lowScore,
            @Param("highScore") Integer highScore,
            Pageable pageable
    );

    @Query("SELECT COUNT(ps) FROM PlayerSeason ps WHERE ps.score > :score AND ps.season.id = :seasonId")
    int countAllByScoreGreaterThan(@Param("score") int score, @Param("seasonId") Long seasonId);



}
