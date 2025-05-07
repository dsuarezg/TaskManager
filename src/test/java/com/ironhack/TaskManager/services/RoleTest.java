package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.Role;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RoleTest {

    private Role role;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        System.out.println("Role created: " + role.getName());

        roleRepository.save(role);
    }

    @AfterEach
    void tearDown() {
   //     roleRepository.delete(role);
    }

    @Test
    @DisplayName("Generate a Role")
    public void testGenerateRole() {
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        roleRepository.save(role);
    }



}
