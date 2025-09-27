package com.example.userservice.mapper;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .age(u.getAge())
                .createdAt(u.getCreatedAt())
                .build();
    }

    public User fromCreateDto(CreateUpdateUserDto d) {
        return User.builder()
                .name(d.getName())
                .email(d.getEmail())
                .age(d.getAge())
                .build();
    }

    public void apply(CreateUpdateUserDto d, User target) {
        if (d.getName() != null) target.setName(d.getName());
        if (d.getEmail() != null) target.setEmail(d.getEmail());
        if (d.getAge() != null) target.setAge(d.getAge());
    }
}