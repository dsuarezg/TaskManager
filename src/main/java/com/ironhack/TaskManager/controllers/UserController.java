package com.ironhack.TaskManager.controllers;

import com.ironhack.TaskManager.exceptions.UserNotFoundException;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/id/{id}")
    @Operation(summary = "Get user by ID")
    @Parameter(name = "id", description = "ID of the user to retrieve")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @Parameter(name = "username", description = "Username of the user to retrieve")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return userService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }


}
