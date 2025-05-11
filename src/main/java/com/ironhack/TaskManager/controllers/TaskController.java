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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/personal/create") // Endpoint to create a personal task
    @Operation(summary = "Create a new personal task") // Swagger documentation for the endpoint
    @ApiResponses(value = { // Defines possible HTTP responses
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public UserTask createPersonalTask(@RequestBody PersonalTask task, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username
        return personalTaskService.createPersonalTask(task, username); // Creates the task for the user
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/personal/list") // Endpoint to list personal tasks
    @Operation(summary = "Get all personal tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<List<PersonalTask>> getPersonalTasks(Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // If the user has the ROLE_ADMIN, return all tasks
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            List<PersonalTask> allTasks = personalTaskService.getAllPersonalTask();
            return ResponseEntity.ok(allTasks);
        }

        // Otherwise, return only the tasks belonging to the user
        List<PersonalTask> tasks = personalTaskService.getPersonalTasksByUsername(username);

//        // Validate ownership of each task
//        for (PersonalTask task : tasks) {
//            taskService.validateTaskOwnership(task.getId(), username);
//        }

        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/personal/delete/{id}") // Endpoint to delete a personal task
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void personalDeleteTask(@PathVariable Long id, Authentication auth) throws AccessDeniedException {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN or ROLE_MANAGER, allow deletion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") )) {
            personalTaskService.deleteTask(id);
            return;
        }

        // Otherwise, verify task ownership before deletion
        if (!personalTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new AccessDeniedException("You are not authorized to delete this task");
        }


        taskService.deleteTask(id); // Deletes the task
    }

    @ExceptionHandler(AccessDeniedException.class) // Handles AccessDeniedException globally for this controller
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // Mandatory Task
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER','USER')")
    @PostMapping("/mandatory/create") // Endpoint to create a mandatory task
    @Operation(summary = "Create a new mandatory task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public UserTask createMandatoryTask(@RequestBody MandatoryTask task, Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username
        return mandatoryTaskService.createMandatoryTask(task, username); // Creates the task for the user
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/mandatory/list") // Endpoint to list mandatory tasks
    @Operation(summary = "Get all mandatory tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<List<MandatoryTask>> getMandatoryTasks(Authentication auth) {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has the ROLE_ADMIN, return all tasks
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            List<MandatoryTask> allTasks = mandatoryTaskService.getAllMandatoryTasks();
            return ResponseEntity.ok(allTasks);
        }

        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_MANAGER"))) {
            List<MandatoryTask> allTasks = mandatoryTaskService.getAllMandatoryTasks();
            return ResponseEntity.ok(allTasks);
        }

        // Otherwise, return only the tasks belonging to the user
        List<MandatoryTask> tasks = mandatoryTaskService.getMandatoryTasksByUsername(username);

//        // Validate ownership of each task
//        for (MandatoryTask task : tasks) {
//            taskService.validateTaskOwnership(task.getId(), username);
//        }

        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER','USER')")
    @DeleteMapping("/mandatory/delete/{id}") // Endpoint to delete a mandatory task
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void mandatoryDeleteTask(@PathVariable Long id, Authentication auth) throws AccessDeniedException {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN, allow deletion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            mandatoryTaskService.deleteTask(id);
            return;
        }

        // Otherwise, verify task ownership before deletion
        if (!mandatoryTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new AccessDeniedException("You are not authorized to delete this task");
        }


        taskService.deleteTask(id); // Deletes the task
    }

    // Task Completion
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PatchMapping("/complete/{id}") // Endpoint to mark a task as completed
    @Operation(summary = "Complete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task completed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void completeTask(@PathVariable Long id, Authentication auth) throws AccessDeniedException {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN or ROLE_MANAGER, allow completion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MANAGER"))) {
            taskService.completeTask(id);
            return;
        }

        // Otherwise, verify task ownership before marking as completed
        if (!mandatoryTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new AccessDeniedException("You are not authorized to complete this task");
        }


        taskService.completeTask(id); // Marks the task as completed
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/delete/{id}") // Endpoint to delete a task
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public void deleteTask(@PathVariable Long id, Authentication auth) throws AccessDeniedException {
        String username = auth.getName(); // Retrieves the authenticated user's username

        // If the user has ROLE_ADMIN or ROLE_MANAGER, allow deletion
        if (auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MANAGER"))) {
            taskService.deleteTask(id);
            return;
        }

        // Otherwise, verify task ownership before deletion
        if (!mandatoryTaskService.verifyByTaskIdAndUsername(id, username)) {
            throw new AccessDeniedException("You are not authorized to delete this task");
        }


        taskService.deleteTask(id); // Deletes the task
    }
}