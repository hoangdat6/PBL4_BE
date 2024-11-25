package org.pbl4.pbl4_be.repositories;

import org.pbl4.pbl4_be.controllers.dto.ProfileDTO;
import org.pbl4.pbl4_be.controllers.dto.UserDTO;
import org.pbl4.pbl4_be.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT new org.pbl4.pbl4_be.controllers.dto.ProfileDTO(u.id, u.name, u.avatar, u.email, u.maxRating, u.lastSeason, u.lastLogin) " +
            "FROM User u " +
            "WHERE u.id = :userId")
    Optional<ProfileDTO> findProfileById(@Param("userId") Long userId);

}
