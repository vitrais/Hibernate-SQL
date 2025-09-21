package com.example.dao;

import com.example.entity.User;
import java.util.List;

public interface UserDAO {
    void save(User user);
    User findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
    boolean existsByEmail(String email);
}
