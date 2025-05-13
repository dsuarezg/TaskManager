package com.ironhack.TaskManager;

import com.ironhack.TaskManager.models.ERole;
import com.ironhack.TaskManager.models.User;
import com.ironhack.TaskManager.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TaskManagerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserService userService;

	@Test
	public void createAndPersistUsers() {

		User admin = new User();
		admin.setUsername("Admin");
		admin.setPassword("Admin1234");
		admin.setRole(ERole.ROLE_ADMIN);

		User manager = new User();
		manager.setUsername("Manager");
		manager.setPassword("Manager1234");
		manager.setRole(ERole.ROLE_MANAGER);

		User user = new User();
		user.setUsername("User");
		user.setPassword("User1234");
		user.setRole(ERole.ROLE_USER);

		User user2 = new User();
		user2.setUsername("User2");
		user2.setPassword("User1234");
		user2.setRole(ERole.ROLE_USER);

		userService.createUser(admin);
		userService.createUser(manager);
		userService.createUser(user);
		userService.createUser(user2);

		Optional<User> persistedAdmin = userService.getByUsername("Admin");
		Optional<User> persistedManager = userService.getByUsername("Manager");
		Optional<User> persistedUser = userService.getByUsername("User");
		Optional<User> persistedUser2 = userService.getByUsername("User2");

		assertNotNull(persistedAdmin);
		assertEquals(ERole.ROLE_ADMIN, persistedAdmin.get().getRole());

		assertNotNull(persistedManager);
		assertEquals(ERole.ROLE_MANAGER, persistedManager.get().getRole());

		assertNotNull(persistedUser);
		assertEquals(ERole.ROLE_USER, persistedUser.get().getRole());

		assertNotNull(persistedUser2);
		assertEquals(ERole.ROLE_USER, persistedUser2.get().getRole());
	}
}
