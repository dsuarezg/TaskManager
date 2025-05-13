package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with an encoded password and saves it to the repository.
     *
     * @param user the user to create; must have a raw (unencrypted) password
     * @return the saved user with the encoded password
     */
    public User createUser(@Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Checks if the provided raw password matches the encoded password of the specified user.
     *
     * @param user the user whose password is being checked
     * @param password the raw password to validate
     * @return true if the raw password matches the user's encoded password; false otherwise
     */
    public boolean passwordIsValid(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for; must not be null or empty
     * @return an Optional containing the user if found, or empty if not found
     * @throws IllegalArgumentException if the username is null or empty
     */
    public Optional<User> getByUsername(String username) {
        if (username == null || username.isEmpty()) {
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
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
    }

    public ResponseEntity<String> authenticateUserNoJSON(User user, JwtService jwtService) {
        Optional<User> optionalUser = getByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordIsValid(existingUser, user.getPassword())) {
                String token = jwtService.generateToken(existingUser);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    /**
     * Loads a user by their username.
     * This method is used by Spring Security during the authentication process.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object containing the user's information.
     * @throws UsernameNotFoundException If the user is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username) // Searches for the user in the database by username.
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Throws an exception if the user is not found.
    }
}
