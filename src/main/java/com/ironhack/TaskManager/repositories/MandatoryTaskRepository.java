package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.MandatoryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MandatoryTaskRepository extends JpaRepository<MandatoryTask, Long> {
}
