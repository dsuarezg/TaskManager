package com.ironhack.TaskManager.controllers;

import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.services.JwtService;
import com.ironhack.TaskManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> optionalUser = userService.getByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (userService.passwordIsValid(existingUser, user.getPassword())) {
                List<String> roleNames = existingUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .toList();

                String token = jwtService.generateToken(existingUser.getUsername(), roleNames.toString());


                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

}

