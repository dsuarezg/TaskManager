package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.ProfessionalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessionalTaskRepository extends JpaRepository<ProfessionalTask, Long> {
}
