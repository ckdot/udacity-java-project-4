package com.example.demo.controllers;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserController userController;

    @Test
    public void findById() {
        String username = "some_username";
        long userId = 123L;
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(123L);
        User responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(username, responseBody.getUsername());
        assertEquals(userId, responseBody.getId());
    }

    @Test
    public void findByUsernameWithNoUserFound() {
        String username = "another_username";

        given(userRepository.findByUsername(username)).willReturn(null);
        ResponseEntity<User> response = userController.findByUserName(username);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void findByUsername() {
        String username = "some_username";
        long userId = 123L;
        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(user);
        ResponseEntity<User> response = userController.findByUserName(username);
        User responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(username, responseBody.getUsername());
        assertEquals(userId, responseBody.getId());
    }

    @Test
    public void createUserWithPasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("short");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void createUserWithPasswordNotEqualToConfirmation() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("first_password");
        request.setConfirmPassword("second_password");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void createUser() {
        String username = "some_username";

        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("some_valid_password");
        request.setConfirmPassword("some_valid_password");
        request.setUsername(username);

        ResponseEntity<User> response = userController.createUser(request);
        User responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(username, responseBody.getUsername());
    }
}
