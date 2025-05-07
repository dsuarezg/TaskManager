package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
