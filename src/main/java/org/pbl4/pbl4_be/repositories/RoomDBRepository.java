package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.RoomDB;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomDBRepository extends JpaRepository<RoomDB, Long> {
    Optional<RoomDB> findByCode(String code);
    boolean existsByCode(String code);

    Optional<RoomDB> findById(Long id);

    List<RoomDB> findAllByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id, Sort createdAt);
    @Query(value = """
    SELECT COUNT(g.id) AS totalGames
    FROM games g
    WHERE DATE_FORMAT(g.created_at, '%d/%m/%Y') = :date
""", nativeQuery = true)
    int countGamesByDate(@Param("date") String date);

    @Query(value = """
    SELECT COUNT(g.id) AS totalGames
    FROM games g
    JOIN rooms r ON g.room_id = r.id
    WHERE r.is_play_online = :isPlayOnline
    AND g.created_at BETWEEN :startDate AND :endDate
""", nativeQuery = true)
    int countGamesByOnlineStatus(
            @Param("isPlayOnline") boolean isPlayOnline,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
        SELECT COUNT(DISTINCT player_id)
        FROM (
            SELECT player1_id AS player_id
            FROM rooms
            WHERE created_at BETWEEN :startDate AND :endDate
            UNION
            SELECT player2_id AS player_id
            FROM rooms
            WHERE created_at BETWEEN :startDate AND :endDate
        ) AS players
    """, nativeQuery = true)
    int countDistinctPlayers(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}
