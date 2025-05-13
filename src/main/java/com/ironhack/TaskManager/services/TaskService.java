package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.exceptions.TaskNotFoundException;
import com.ironhack.TaskManager.models.Task;
import com.ironhack.TaskManager.repositories.TaskRepository;
import com.ironhack.TaskManager.repositories.UserTaskRepository;
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


}
