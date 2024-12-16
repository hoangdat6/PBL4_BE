package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.RoomDB;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomDBRepository extends JpaRepository<RoomDB, Long> {
    Optional<RoomDB> findByCode(String code);
    boolean existsByCode(String code);

    Optional<RoomDB> findById(Long id);

    List<RoomDB> findAllByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id, Sort createdAt);

}
