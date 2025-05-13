package com.ironhack.TaskManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class AuthorizationService {

    @Autowired
    private MandatoryTaskService mandatoryTaskService;

    public boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role));
    }

    public void validateOwnershipOrAdmin(Authentication auth, Long taskId) throws AccessDeniedException {
        if (!hasRole(auth, "ROLE_ADMIN") && !mandatoryTaskService.verifyByTaskIdAndUsername(taskId, auth.getName())) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }
    }
}