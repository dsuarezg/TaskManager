package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(@Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean passwordIsValid(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Optional<User> getByUsername(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.findByUsername(username);
    }

    public ResponseEntity<?> authenticateUser(User user, JwtService jwtService) {
        Optional<User> optionalUser = getByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordIsValid(existingUser, user.getPassword())) {
                String token = jwtService.generateToken(existingUser);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
