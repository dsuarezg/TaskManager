package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
}
