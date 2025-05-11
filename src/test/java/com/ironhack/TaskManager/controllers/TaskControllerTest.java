package com.ironhack.TaskManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.MandatoryTask;
import com.ironhack.TaskManager.models.PersonalTask;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.repositories.UserRepository;
import com.ironhack.TaskManager.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clear the database and create a valid user before each test
        userRepository.deleteAll();

        User validUser = new User();
        validUser.setUsername("ValidUser");
        validUser.setPassword("Valid1234");
        validUser.setRole(ERole.ROLE_USER);

        userService.createUser(validUser);
    }

    // Helper method to authenticate a user and retrieve a token
    private String authenticateUser(String username, String password) throws Exception {
        User loginRequest = new User();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return "Bearer " + objectMapper.readTree(response).get("token").asText();
    }

    // Helper method to create a personal task
    private String createPersonalTask(String token, String description, int duration, String place) throws Exception {
        PersonalTask personalTask = new PersonalTask();
        personalTask.setDescription(description);
        personalTask.setDuration(duration);
        personalTask.setPlace(place);

        String response = mockMvc.perform(post("/api/task/personal/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personalTask))
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.description").value(description))
                .andExpect(jsonPath("$.task.duration").value(duration))
                .andExpect(jsonPath("$.task.place").value(place))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response; // Return the response if additional validations are needed
    }

    // Helper method to create a mandatory task
    private String createMandatoryTask(String token, String description, int duration, String place, String department) throws Exception {
        MandatoryTask mandatoryTask = new MandatoryTask();
        mandatoryTask.setDescription(description);
        mandatoryTask.setStartDate(LocalDate.now());
        mandatoryTask.setDueDate(LocalDate.now().plusDays(5));
        mandatoryTask.setAssignedTo("ValidUser");
        mandatoryTask.setPriority("HIGH");

        String response = mockMvc.perform(post("/api/task/mandatory/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mandatoryTask))
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.description").value(description))
                .andExpect(jsonPath("$.task.startDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.task.dueDate").value(LocalDate.now().plusDays(5).toString()))
                .andExpect(jsonPath("$.task.assignedTo").value("ValidUser"))
                .andExpect(jsonPath("$.task.priority").value("HIGH"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response; // Return the response if additional validations are needed
    }

    @Test
    @DisplayName("User can create a personal task")
    public void userCanCreatePersonalTask() throws Exception {
        // Authenticate the user and get a token
        String token = authenticateUser("ValidUser", "Valid1234");

        // Create a personal task
        createPersonalTask(token, "Personal task description", 45, "Office");

        // Verify the task was created
        mockMvc.perform(get("/api/task/personal/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Personal task description"))
                .andExpect(jsonPath("$[0].duration").value(45))
                .andExpect(jsonPath("$[0].place").value("Office"));
    }

    @Test
    @DisplayName("User can create a mandatory task")
    public void userCanCreateMandatoryTask() throws Exception {
        // Authenticate the user and get a token
        String token = authenticateUser("ValidUser", "Valid1234");

        // Create a mandatory task
        createMandatoryTask(token, "Mandatory task description", 60, "Headquarters", "HR");

        // Verify the task was created
        mockMvc.perform(get("/api/task/mandatory/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Mandatory task description"))
                .andExpect(jsonPath("$[0].startDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].dueDate").value(LocalDate.now().plusDays(5).toString()))
                .andExpect(jsonPath("$[0].assignedTo").value("ValidUser"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    @DisplayName("User who created the task can read it")
    public void userWhoCreatedTask_CanReadIt() throws Exception {
        // Authenticate the user and get a token
        String token = authenticateUser("ValidUser", "Valid1234");

        // Create a personal task
        createPersonalTask(token, "Test personal task", 60, "Home");

        // Retrieve the task and verify its details
        mockMvc.perform(get("/api/task/personal/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test personal task"))
                .andExpect(jsonPath("$[0].duration").value(60))
                .andExpect(jsonPath("$[0].place").value("Home"));
    }

    @Test
    @DisplayName("Another user cannot read the task")
    public void anotherUser_CannotReadTask() throws Exception {
        // Authenticate the first user and get a token
        String token = authenticateUser("ValidUser", "Valid1234");

        // Create a personal task
        createPersonalTask(token, "Test personal task", 60, "Home");

        // Create a second user
        User secondUser = new User();
        secondUser.setUsername("SecondUser");
        secondUser.setPassword("Second1234");
        secondUser.setRole(ERole.ROLE_USER);
        userService.createUser(secondUser);

        // Authenticate the second user and get a token
        String secondUserToken = authenticateUser("SecondUser", "Second1234");

        // Ensure the tokens are different
        assertNotEquals(token, secondUserToken);

        // Attempt to retrieve the task with the second user's token and verify the list is empty
        mockMvc.perform(get("/api/task/personal/list")
                        .header("Authorization", secondUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}