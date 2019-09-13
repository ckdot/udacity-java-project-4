package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserDetailsServiceImplTest {

    @MockBean
    private UserRepository applicationUserRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadByUsernameWithUserNotFound() {
        String username = "some_username";
        given(applicationUserRepository.findByUsername(username)).willReturn(null);
        UsernameNotFoundException exception = null;

        try {
            userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void loadByUsername() {
        String username = "another_username";
        String password = "random_password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        given(applicationUserRepository.findByUsername(username)).willReturn(user);
        UserDetails details = userDetailsService.loadUserByUsername(username);

        assertEquals(details.getUsername(), user.getUsername());
        assertEquals(details.getPassword(), user.getPassword());
    }
}
