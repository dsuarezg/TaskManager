package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(@Valid User user) {
        return userRepository.save(user);
    }

    public Optional<User> getByUsername(String username) {
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.findByUsername(username);
    }
}
