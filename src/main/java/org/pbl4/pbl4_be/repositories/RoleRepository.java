package org.pbl4.pbl4_be.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.pbl4.pbl4_be.models.Role;
import org.pbl4.pbl4_be.enums.ERole;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}