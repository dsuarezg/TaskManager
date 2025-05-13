package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.exceptions.UserNotFoundException;
import com.ironhack.TaskManager.models.MandatoryTask;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.repositories.MandatoryTaskRepository;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.repositories.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MandatoryTaskService extends TaskService {

    // Injects the UserTaskRepository to handle UserTask-related database operations
    @Autowired
    private UserTaskRepository userTaskRepository;

    // Injects the UserRepository to handle User-related database operations
    @Autowired
    private UserRepository userRepository;

    // Injects the MandatoryTaskRepository to handle MandatoryTask-related database operations
    @Autowired
    private MandatoryTaskRepository mandatoryTaskRepository;

    /**
     * Creates a new mandatory task for a specific user.
     *
     * @param task     The mandatory task to be created.
     * @param username The username of the user to whom the task will be assigned.
     * @return The UserTask object that links the user and the task.
     */
    public UserTask createMandatoryTask(MandatoryTask task, String username) {

        // Finds the user by username or throws an exception if the user is not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Sets the start date of the task to the current date
        task.setStartDate(LocalDate.now());

        // Sets the assigned user for the task
        task.setAssignedTo(user.getUsername());

        // Saves the mandatory task to the database
        MandatoryTask savedTask = mandatoryTaskRepository.save(task);

        // Creates a UserTask object to link the user and the task
        UserTask userTask = new UserTask();
        userTask.setUser(user); // Sets the user
        userTask.setTask(savedTask); // Sets the task

        // Saves and returns the UserTask object
        return userTaskRepository.save(userTask);
    }

    /**
     * Retrieves all mandatory tasks assigned to a specific user.
     *
     * @param username The username of the user whose tasks are to be retrieved.
     * @return A list of MandatoryTask objects assigned to the user.
     */
    public List<MandatoryTask> getMandatoryTasksByUsername(String username) {
        // Finds all UserTask objects associated with the given username
        List<UserTask> userTasks = userTaskRepository.findByUser_Username(username);

        // Maps the UserTask objects to MandatoryTask objects, filters out null values, and collects them into a list
        return userTasks.stream()
                .map(UserTask::getTask) // Maps the task to its corresponding UserTask
                .filter(task -> task instanceof MandatoryTask) // Filters out tasks that are not MandatoryTask
                .map(task -> (MandatoryTask) task) // Casts the task to MandatoryTask
                .collect(Collectors.toList()); // Collects the tasks into a list
    }


    public List<MandatoryTask> getAllMandatoryTasks() {
        return mandatoryTaskRepository.findAll();
    }


}