package com.example.service;

import com.example.dao.UserDAO;
import com.example.entity.User;
import com.example.exception.DuplicateEmailException;
import com.example.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setName("Test User");

        when(userDAO.existsByEmail(any())).thenReturn(false);

        User created = userService.createUser(newUser);

        assertNotNull(created);
        verify(userDAO).save(newUser);
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        User existingUser = new User();
        existingUser.setEmail("exists@example.com");

        when(userDAO.existsByEmail(existingUser.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(existingUser));
    }

    @Test
    void shouldFindUserById() {
        User user = new User();
        user.setId(1L);

        when(userDAO.findById(1L)).thenReturn(user);

        User found = userService.getUserById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userDAO.findById(any())).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(999L));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old Name");

        when(userDAO.findById(1L)).thenReturn(existing);
        doNothing().when(userDAO).update(any());

        User updated = userService.updateUser(1L, "New Name", null, null);
        assertEquals("New Name", updated.getName());
    }

    @Test
    void shouldReturnAllUsers() {
        when(userDAO.findAll()).thenReturn(Collections.singletonList(new User()));

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void shouldCreateUserWithGeneratedId() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@example.com");

        doAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return null;
        }).when(userDAO).save(any(User.class));

        userService.createUser(newUser);

        assertThat(newUser.getId()).isEqualTo(1L);
        verify(userDAO).save(newUser);
    }

}