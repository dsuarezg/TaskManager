package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.exceptions.TaskNotFoundException;
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
import java.util.stream.Collectors;

@Service
public class PersonalTaskService extends TaskService {

    // Injects the UserTaskRepository to handle UserTask-related database operations
    @Autowired
    private UserTaskRepository userTaskRepository;

    // Injects the UserRepository to handle User-related database operations
    @Autowired
    private UserRepository userRepository;

    // Injects the PersonalTaskRepository to handle PersonalTask-related database operations
    @Autowired
    private PersonalTaskRepository personalTaskRepository;

    /**
     * Creates and assigns a new personal task to a user.
     *
     * Associates the provided personal task with the user identified by the given username,
     * persists both the task and the user-task relationship, and returns the resulting UserTask.
     *
     * @param task the personal task to assign
     * @param username the username of the user to assign the task to
     * @return the UserTask entity linking the user and the new personal task
     * @throws UserNotFoundException if the specified user does not exist
     */
    public UserTask createPersonalTask(PersonalTask task, String username) {

        // Finds the user by username or throws an exception if the user is not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Saves the personal task to the database
        PersonalTask savedTask = personalTaskRepository.save(task);

        // Creates a UserTask object to link the user and the task
        UserTask userTask = new UserTask();
        userTask.setUser(user); // Sets the user
        userTask.setTask(savedTask); // Sets the task
        userTask.setTaskType(task.getClass().getSimpleName()); // Adds task type

        // Saves and returns the UserTask object
        return userTaskRepository.save(userTask);
    }

    /**
     * Retrieves all personal tasks assigned to the specified user.
     *
     * @param username the username whose personal tasks are to be retrieved
     * @return a list of PersonalTask entities associated with the user
     */
    public List<PersonalTask> getPersonalTasksByUsername(String username) {
        // Finds all UserTask objects associated with the given username
        List<UserTask> userTasks = userTaskRepository.findByUser_Username(username);

        // Maps the UserTask objects to PersonalTask objects, filters out null values, and collects them into a list
        return userTasks.stream() // Maps the UserTask objects to Task objects
                .map(UserTask::getTask)  //
                .filter(task -> task instanceof PersonalTask) // Filters out non-PersonalTask objects
                .map(task -> (PersonalTask) task) // Casts the Task objects to PersonalTask
                .collect(Collectors.toList()); // Collects the tasks into a list
    }


    /**
     * Returns a list of all personal tasks stored in the repository.
     *
     * @return a list of all PersonalTask entities
     */
    public List<PersonalTask> getAllPersonalTask() {
        return personalTaskRepository.findAll();
    }


    /**
     * Sets the finished status of a personal task identified by its ID.
     *
     * @param taskId the ID of the personal task to update
     * @param finished true to mark the task as finished, false to mark as unfinished
     * @throws TaskNotFoundException if no personal task with the specified ID exists
     */
    public void completeTask(Long taskId, boolean finished) {
        PersonalTask task = personalTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setFinished(finished);
        personalTaskRepository.save(task);
    }

}