package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.models.RoomDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoomDBRepository extends JpaRepository<RoomDB, Long> {
    Optional<RoomDB> findByCode(String code);
    boolean existsByCode(String code);

    Optional<RoomDB> findById(Long id);

}
