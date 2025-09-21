package com.example.dao;

import com.example.dao.impl.UserDAOImpl;
import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("jdbc:postgresql://localhost:5432/postgres")
            .withUsername("postgres")
            .withPassword("123456");

    private UserDAO userDAO;

    @BeforeAll
    void setup() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        HibernateUtil.getSessionFactory();
        userDAO = new UserDAOImpl();
    }

    @BeforeEach
    void cleanDatabase() {
        userDAO.findAll().forEach(user -> userDAO.delete(user.getId()));
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(30);

        userDAO.save(user);
        assertNotNull(user.getId());

        User found = userDAO.findById(user.getId());
        assertAll(
                () -> assertEquals(user.getName(), found.getName()),
                () -> assertEquals(user.getEmail(), found.getEmail()),
                () -> assertEquals(user.getAge(), found.getAge())
        );
    }

    @Test
    void shouldUpdateUser() {
        User user = createTestUser();
        userDAO.save(user);

        user.setName("Updated Name");
        userDAO.update(user);

        User updated = userDAO.findById(user.getId());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void shouldDeleteUser() {
        User user = createTestUser();
        userDAO.save(user);

        userDAO.delete(user.getId());
        assertNull(userDAO.findById(user.getId()));
    }

    @Test
    void shouldFindAllUsers() {
        userDAO.save(createTestUser());
        userDAO.save(createTestUser());

        List<User> users = userDAO.findAll();
        assertEquals(2, users.size());
    }

    private User createTestUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test" + System.currentTimeMillis() + "@example.com");
        user.setAge(30);
        return user;
    }
}
