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

    public Boolean verifyByTaskIdAndUsername(Long taskId, String username) {
        return userTaskRepository.existsByTask_IdAndUser_Username(taskId, username);
    }

    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setFinished(true);
        taskRepository.save(task);
    }

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
