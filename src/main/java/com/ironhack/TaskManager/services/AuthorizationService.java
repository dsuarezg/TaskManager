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
     * Determines whether the provided authentication includes the specified role.
     *
     * @param auth the authentication object whose authorities are checked
     * @param role the role name to search for among the authentication's authorities
     * @return true if the authentication contains the specified role; false otherwise
     */
    public boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role));
    }

}