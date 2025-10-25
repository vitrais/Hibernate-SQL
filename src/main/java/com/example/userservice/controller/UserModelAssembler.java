package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {
    @Override
    public EntityModel<UserDto> toModel(UserDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUser(dto.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).listUsers(0, 10)).withRel("users"));
    }
}
