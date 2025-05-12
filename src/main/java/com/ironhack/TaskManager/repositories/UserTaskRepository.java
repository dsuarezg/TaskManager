package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUser_Username(String username);

    Boolean existsByTask_IdAndUser_Username(Long taskId, String username);

    void deleteByTaskId(Long taskId);
}
