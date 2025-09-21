package com.example.service;

import com.example.dao.UserDAO;
import com.example.entity.User;
import com.example.exception.DuplicateEmailException;
import com.example.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User createUser(User user) {
        validateUserForCreation(user);

        user.setCreatedAt(LocalDateTime.now());
        userDAO.save(user);
        return user;
    }

    public User getUserById(Long id) {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        return user;
    }

    public User updateUser(Long id, String newName, String newEmail, Integer newAge) {
        User existingUser = getUserById(id);

        if (newEmail != null && !newEmail.equals(existingUser.getEmail())) {
            checkEmailUniqueness(newEmail);
        }

        applyUpdates(existingUser, newName, newEmail, newAge);
        userDAO.update(existingUser);
        return existingUser;
    }

    public void deleteUser(Long id) {
        if (!userExists(id)) {
            throw new UserNotFoundException("Cannot delete - user not found with ID: " + id);
        }
        userDAO.delete(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    private void validateUserForCreation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        checkEmailUniqueness(user.getEmail());

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (user.getAge() != null && user.getAge() < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    private void checkEmailUniqueness(String email) {
        if (userDAO.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered: " + email);
        }
    }

    private void applyUpdates(User user, String newName, String newEmail, Integer newAge) {
        if (newName != null && !newName.isBlank()) {
            user.setName(newName.trim());
        }

        if (newEmail != null && !newEmail.isBlank()) {
            user.setEmail(newEmail.trim());
        }

        if (newAge != null) {
            if (newAge < 0) throw new IllegalArgumentException("Age cannot be negative");
            user.setAge(newAge);
        }
    }

    private boolean userExists(Long id) {
        return userDAO.findById(id) != null;
    }
}