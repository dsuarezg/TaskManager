package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.exceptions.UserNotFoundException;
import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.repositories.PersonalTaskRepository;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.repositories.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserTaskService {

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalTaskRepository personalTaskRepository;


public UserTask createUserPersonalTask(PersonalTask task, String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));


    PersonalTask savedTask = personalTaskRepository.save(task);

    UserTask userTask = new UserTask();
    userTask.setUser(user);
    userTask.setTask(savedTask);
    userTask.setComments(task.getDescription());

    return userTaskRepository.save(userTask);
}

    public List<PersonalTask> getPersonalTasksByUsername(String username) {
        List<UserTask> userTasks = userTaskRepository.findByUser_Username(username);
        return userTasks.stream()
                .map(userTask -> (PersonalTask) userTask.getTask())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
