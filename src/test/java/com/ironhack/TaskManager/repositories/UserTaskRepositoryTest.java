package com.ironhack.TaskManager.repositories;

import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.services.PersonalTaskService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserTaskRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalTaskService personalTaskService;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Test
    @DisplayName("Assign a personal task to a user")
    void whenAssignPersonalTaskToUser_thenUserTaskIsCreated() {

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user = userRepository.save(user);

        PersonalTask task = new PersonalTask();
        task.setDescription("Test Task");
        task.setDuration(50);
        task.setPlace("The Office");

        UserTask userTask = personalTaskService.createPersonalTask(task, user.getUsername());

        assertNotNull(userTask);
        assertTrue(userTaskRepository.existsByTask_IdAndUser_Username(task.getId(), user.getUsername()));
        assertEquals(user.getId(), userTask.getUser().getId());
        assertEquals(task.getDescription(), userTask.getTask().getDescription());
    }
}