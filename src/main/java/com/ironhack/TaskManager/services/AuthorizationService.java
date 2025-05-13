package com.ironhack.TaskManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class AuthorizationService {

    @Autowired
    private MandatoryTaskService mandatoryTaskService;

    /**
     * Checks if the given authentication contains the specified role.
     *
     * @param auth the authentication object to check
     * @param role the role to look for (e.g., "ROLE_ADMIN")
     * @return true if the authentication has the specified role, false otherwise
     */
    public boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role));
    }

}