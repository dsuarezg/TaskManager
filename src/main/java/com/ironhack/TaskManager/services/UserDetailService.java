package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.repositories.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class that implements the Spring Security UserDetailsService interface.
 * This class is responsible for loading user-specific data during authentication.
 */
@Service
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods.
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required (final) fields.
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Repository to interact with the user data in the database.

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