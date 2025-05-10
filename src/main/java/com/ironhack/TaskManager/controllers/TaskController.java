package com.ironhack.TaskManager.controllers;

import com.ironhack.TaskManager.models.MandatoryTask;
import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.services.MandatoryTaskService;
import com.ironhack.TaskManager.services.PersonalTaskService;
import com.ironhack.TaskManager.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController // Marks this class as a REST controller, handling HTTP requests
@RequestMapping("/api/task") // Base URL for all endpoints in this controller
public class TaskController {

    @Autowired // Automatically injects the required service beans
    private PersonalTaskService personalTaskService;
    @Autowired
    private MandatoryTaskService mandatoryTaskService;
    @Autowired
    private TaskService taskService;

    // Personal Task
    @PostMapping("/personal/create") // Endpoint to create a personal task
    @Operation(summary = "Create a new personal task") // Swagger documentation for the endpoint
    @ApiResponses(value = { // Defines possible HTTP responses
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public UserTask createPersonalTask(@RequestBody PersonalTask task, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username
        return personalTaskService.createPersonalTask(task, username); // Creates the task for the user
    }

    @GetMapping("/personal/list") // Endpoint to list personal tasks
    @Operation(summary = "Get all personal tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<List<PersonalTask>> getPersonalTasks(Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has the ROLE_ADMIN, return all tasks
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            List<PersonalTask> allTasks = personalTaskService.getAllPersonalTask();
            return ResponseEntity.ok(allTasks);
        }

        // Otherwise, return only the tasks belonging to the user
        List<PersonalTask> tasks = personalTaskService.getPersonalTasksByUsername(username);

        // Validate ownership of each task
        for (PersonalTask task : tasks) {
            personalTaskService.validateTaskOwnership(task.getId(), username);
        }

        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/personal/delete/{id}") // Endpoint to delete a personal task
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void personalDeleteTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN or ROLE_MANAGER, allow deletion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MANAGER"))) {
            personalTaskService.deleteTask(id);
            return;
        }

        // Otherwise, verify task ownership before deletion
        if (!personalTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new IllegalArgumentException("Task does not belong to the authenticated user");
        }

        taskService.deleteTask(id); // Deletes the task
    }

    @ExceptionHandler(AccessDeniedException.class) // Handles AccessDeniedException globally for this controller
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // Mandatory Task
    @PostMapping("/mandatory/create") // Endpoint to create a mandatory task
    @Operation(summary = "Create a new mandatory task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public UserTask createMandatoryTask(@RequestBody MandatoryTask task, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username
        return mandatoryTaskService.createMandatoryTask(task, username); // Creates the task for the user
    }

    @GetMapping("/mandatory/list") // Endpoint to list mandatory tasks
    @Operation(summary = "Get all mandatory tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public List<MandatoryTask> getMandatoryTasks(Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username
        return mandatoryTaskService.getMandatoryTasksByUsername(username); // Returns tasks for the user
    }

    @DeleteMapping("/mandatory/delete/{id}") // Endpoint to delete a mandatory task
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void mandatoryDeleteTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN, allow deletion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            mandatoryTaskService.deleteTask(id);
            return;
        }

        // Otherwise, verify task ownership before deletion
        if (!mandatoryTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new IllegalArgumentException("Task does not belong to the authenticated user");
        }

        taskService.deleteTask(id); // Deletes the task
    }

    // Task Completion
    @PatchMapping("/complete/{id}") // Endpoint to mark a task as completed
    @Operation(summary = "Complete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task completed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void completeTask(@PathVariable Long id, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN or ROLE_MANAGER, allow completion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MANAGER"))) {
            taskService.completeTask(id);
            return;
        }

        // Otherwise, verify task ownership before marking as completed
        if (!taskService.verifyByTaskIdAndUsername(id, username)) {
            throw new IllegalArgumentException("Task does not belong to the authenticated user");
        }

        taskService.completeTask(id); // Marks the task as completed
    }
}