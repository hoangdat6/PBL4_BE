package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.controllers.dto.ProfileDTO;
import org.pbl4.pbl4_be.controllers.dto.UserDTO;
import org.pbl4.pbl4_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    Optional<User> findUserById(@Param("id") Long id);

    @Query(value = """
    SELECT 
        u.id AS userId, 
        u.name AS userName, 
        u.email AS userEmail, 
        u.created_at AS userCreatedAt,
        COUNT(g.id) AS totalGames,
        SUM(CASE WHEN g.winner_id IS NULL THEN 1 ELSE 0 END) AS totalDraws,
        SUM(CASE WHEN g.winner_id = u.id THEN 1 ELSE 0 END) AS totalWins,
        SUM(CASE WHEN g.winner_id IS NOT NULL AND g.winner_id != u.id THEN 1 ELSE 0 END) AS totalLoses
    FROM users u
    LEFT JOIN rooms r ON (r.player1_id = u.id OR r.player2_id = u.id)
    LEFT JOIN games g ON g.room_id = r.id
    GROUP BY u.id
    ORDER BY 
        CASE WHEN :sortColumn = 'win' THEN totalWins END DESC,
        CASE WHEN :sortColumn = 'lose' THEN totalLoses END DESC,
        CASE WHEN :sortColumn = 'draw' THEN totalDraws END DESC,
        CASE WHEN :sortColumn = 'total' THEN totalGames END DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> getPlayerStatisticsWithPagination(
            @Param("sortColumn") String sortColumn,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(DISTINCT u.id)
    FROM users u
    LEFT JOIN rooms r ON (r.player1_id = u.id OR r.player2_id = u.id)
    LEFT JOIN games g ON g.room_id = r.id
""", nativeQuery = true)
    int countTotalPlayers();


}
