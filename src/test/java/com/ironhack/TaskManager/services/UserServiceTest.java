package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {

    private User user;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("UserTest");
        user.setPassword("Test1234");
        user.setRole(ERole.ROLE_USER);
        System.out.println("User created: " + user);

        userService.createUser(user);
    }

    @AfterEach
    void tearDown() {
        //userRepository.deleteAll();
        //userRepository.delete(user);
    }

    @Test
    @DisplayName("Generate a user")
    public void testGenerateUser() {
        User user = new User();
        user.setUsername("User");
        user.setPassword("User1234");
        user.setRole(ERole.ROLE_USER);

        userService.createUser(user);
    }

    @Test
    @DisplayName("Encription of password successful")
    public void testEncriptionOfPasswordSuccessful() {
        assertTrue(user.getPassword().startsWith("$2a$"));// BCrypt password starts with $2a$
        System.out.println("Password encrypted: " + user.getPassword());
    }

    @Test
    @DisplayName("User role is assigned correctly")
    public void testUserRoleIsAssignedCorrectly() {
        user.setUsername("User");
        user.setPassword("User1234");
        user.setRole(ERole.ROLE_USER);
        assertNotNull(user, "User not found");
        assertNotNull(user.getRole(), "Role is null");
        assertEquals(ERole.ROLE_USER, user.getRole(), "Role is not assigned correctly");
    }

    @Test
    @DisplayName("Admin role is assigned correctly")
    public void testAdminRoleIsAssignedCorrectly() {
        user.setUsername("Admin");
        user.setPassword("Admin1234");
        user.setRole(ERole.ROLE_ADMIN);
        userService.createUser(user);
        assertNotNull(user, "User not found");
        assertNotNull(user.getRole(), "Role is null");
        assertEquals(ERole.ROLE_ADMIN, user.getRole(), "Role is not assigned correctly");
    }

    @Test
    @DisplayName("Manager role is assigned correctly")
    public void testManagerRoleIsAssignedCorrectly() {
        user.setUsername("Manager");
        user.setPassword("Manager1234");
        user.setRole(ERole.ROLE_MANAGER);
        userService.createUser(user);
        assertNotNull(user, "User not found");
        assertNotNull(user.getRole(), "Role is null");
        assertEquals(ERole.ROLE_MANAGER, user.getRole(), "Role is not assigned correctly");
    }



}