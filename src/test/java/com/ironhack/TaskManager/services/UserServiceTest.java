package com.ironhack.TaskManager.services;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setUsername("UserTest");
        user.setPassword("Test1234");
        user.setRole(ERole.ROLE_USER);

        userService.createUser(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Contraseña encriptada correctamente")
    public void testPasswordEncryption() {
        User savedUser = userRepository.findByUsername("UserTest").orElse(null);
        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches("Test1234", savedUser.getPassword()));
    }

    @Test
    @DisplayName("Validación de contraseña exitosa")
    public void testPasswordValidationSuccess() {
        User savedUser = userRepository.findByUsername("UserTest").orElse(null);
        assertNotNull(savedUser);
        assertTrue(userService.passwordIsValid(savedUser, "Test1234"));
    }

    @Test
    @DisplayName("Validación de contraseña fallida")
    public void testPasswordValidationFailure() {
        User savedUser = userRepository.findByUsername("UserTest").orElse(null);
        assertNotNull(savedUser);
        assertFalse(userService.passwordIsValid(savedUser, "WrongPassword"));
    }
}