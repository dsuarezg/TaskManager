package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.exceptions.TaskNotFoundException;
import com.ironhack.TaskManager.exceptions.UserNotFoundException;
import com.ironhack.TaskManager.models.MandatoryTask;
import com.ironhack.TaskManager.models.Task;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.repositories.TaskRepository;
import com.ironhack.TaskManager.repositories.UserTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    /**
     * Checks if a user-task association exists for the specified task ID and username.
     *
     * @param taskId   the ID of the task
     * @param username the username to check association with the task
     * @return {@code true} if the association exists, {@code false} otherwise
     */
    public Boolean verifyByTaskIdAndUsername(Long taskId, String username) {
        return userTaskRepository.existsByTask_IdAndUser_Username(taskId, username);
    }

    /**
     * Marks a task as finished by its ID.
     *
     * @param taskId the ID of the task to mark as completed
     * @throws TaskNotFoundException if no task with the specified ID exists
     */
    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setFinished(true);
        taskRepository.save(task);
    }


    /**
     * Deletes a task and all associated user-task relationships by task ID.
     *
     * @param taskId the ID of the task to delete
     * @throws TaskNotFoundException if the task with the specified ID does not exist
     */
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        userTaskRepository.deleteByTaskId(taskId);
        taskRepository.delete(task);
    }

    public void validateTaskOwnership(Long taskId, String username) {
        // Verifica si existe una relación entre el usuario y la tarea en UserTask
        boolean exists = userTaskRepository.existsByTask_IdAndUser_Username(taskId, username);
        if (!exists) {
            throw new IllegalArgumentException ("You are not authorized to access this task.");
        }
    }


    }
