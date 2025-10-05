package com.example.userservice.service;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.userservice.exception.NotFoundEx;
import java.util.List;
import com.example.userservice.event.UserEvent;
import com.example.userservice.messaging.UserEventPublisher;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final UserEventPublisher publisher;

    @Transactional
    public UserDto create(CreateUpdateUserDto dto) {
        User saved = repo.save(UserMapper.fromCreateDto(dto));
        publisher.publish(UserEvent.Operation.CREATED, saved.getId(), saved.getEmail(), saved.getName());
        return UserMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        return UserMapper.toDto(findEntity(id));
    }

    @Transactional(readOnly = true)
    public List<UserDto> list() {
        return repo.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Transactional
    public UserDto update(Long id, CreateUpdateUserDto dto) {
        User u = findEntity(id);
        UserMapper.apply(dto, u);
        return UserMapper.toDto(repo.save(u));
    }

    @Transactional
    public void delete(Long id) {
        User u = findEntity(id);
        repo.deleteById(id);
        publisher.publish(UserEvent.Operation.DELETED, u.getId(), u.getEmail(), u.getName());
    }

    private User findEntity(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundEx("User not found: " + id));
    }


}