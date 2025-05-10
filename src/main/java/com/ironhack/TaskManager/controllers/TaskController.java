package com.ironhack.TaskManager.controllers;

import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.UserTask;
import com.ironhack.TaskManager.services.UserTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private UserTaskService userTaskService;


    @PostMapping("/personal/create")
    @Operation(summary = "Create a new personal task")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public UserTask createTask(@RequestBody PersonalTask task, Authentication auth) {
        String username = auth.getName();
        return userTaskService.createUserPersonalTask(task, username);
    }

    @GetMapping("/personal/list")
    @Operation(summary = "Get all personal tasks for the authenticated user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden")})
    public List<PersonalTask> getUserTasks(Authentication auth) {
        String username = auth.getName();
        return userTaskService.getPersonalTasksByUsername(username);
    }

}
