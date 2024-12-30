package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long>{
    @Query("SELECT s FROM Season s WHERE s.startDate <= CURRENT_TIMESTAMP AND s.endDate >= CURRENT_TIMESTAMP")
    Optional<Season> findCurrentSeason();

}
