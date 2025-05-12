package com.ironhack.TaskManager.controllers;

import com.ironhack.TaskManager.models.MandatoryTask;
import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.services.AuthorizationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private PersonalTaskService personalTaskService;
    @Autowired
    private MandatoryTaskService mandatoryTaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private AuthorizationService authorizationService;

    /// Personal Tasks
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/personal/create")
    @Operation(summary = "Create a new personal task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<UserTask> createPersonalTask(@RequestBody PersonalTask task, Authentication auth) {
        return ResponseEntity.ok(personalTaskService.createPersonalTask(task, auth.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/personal/list")
    @Operation(summary = "Get all personal tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<List<PersonalTask>> getPersonalTasks(Authentication auth) {
        if (authorizationService.hasRole(auth, "ROLE_MANAGER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (authorizationService.hasRole(auth, "ROLE_ADMIN")) {
            return ResponseEntity.ok(personalTaskService.getAllPersonalTask());
        }
        return ResponseEntity.ok(personalTaskService.getPersonalTasksByUsername(auth.getName()));
    }

    /// Mandatory Tasks
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER','USER')")
    @PostMapping("/mandatory/create")
    @Operation(summary = "Create a new mandatory task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<UserTask> createMandatoryTask(@RequestBody MandatoryTask task, Authentication auth) {
        return ResponseEntity.ok(mandatoryTaskService.createMandatoryTask(task, auth.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/mandatory/list")
    @Operation(summary = "Get all mandatory tasks for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public ResponseEntity<List<MandatoryTask>> getMandatoryTasks(Authentication auth) {
        if (authorizationService.hasRole(auth, "ROLE_ADMIN") || authorizationService.hasRole(auth, "ROLE_MANAGER")) {
            return ResponseEntity.ok(mandatoryTaskService.getAllMandatoryTasks());
        }
        return ResponseEntity.ok(mandatoryTaskService.getMandatoryTasksByUsername(auth.getName()));
    }

    // Task Completion
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PatchMapping("/complete/{id}")
    @Operation(summary = "Complete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task completed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public ResponseEntity<String> completeTask(@PathVariable Long id, Authentication auth) {
        if (!authorizationService.hasRole(auth, "ROLE_ADMIN") &&
                !mandatoryTaskService.verifyByTaskIdAndUsername(id, auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action");
        }
        taskService.completeTask(id);
        return ResponseEntity.ok("Task completed successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    public ResponseEntity<String> deleteTask(@PathVariable Long id, Authentication auth) {
        if (!authorizationService.hasRole(auth, "ROLE_ADMIN") &&
                !mandatoryTaskService.verifyByTaskIdAndUsername(id, auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action");
        }
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
}