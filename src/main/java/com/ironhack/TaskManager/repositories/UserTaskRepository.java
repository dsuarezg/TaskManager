package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.models.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    List<UserTask> findByUser(User user);

    List<UserTask> findByUserId(Long userId);

    List<UserTask> findByUser_Username(String username);

}
