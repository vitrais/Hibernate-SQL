package com.example.userservice.controller;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Users", description = "Операции с пользователями")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserModelAssembler assembler;

    @Operation(summary = "Получить список пользователей")
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> listUsers(
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size) {

        List<EntityModel<UserDto>> users = service.list().stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).listUsers(page, size)).withSelfRel());
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public EntityModel<UserDto> getUser(@PathVariable Long id) {
        UserDto dto = service.get(id);
        return assembler.toModel(dto);
    }

    @Operation(summary = "Создать пользователя")
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> create(@Valid @RequestBody CreateUpdateUserDto dto) {
        UserDto created = service.create(dto);
        EntityModel<UserDto> model = assembler.toModel(created);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + created.getId()))
                .body(model);
    }

    @Operation(summary = "Обновить пользователя по ID")
    @PutMapping("/{id}")
    public EntityModel<UserDto> update(@PathVariable Long id, @Valid @RequestBody CreateUpdateUserDto dto) {
        UserDto updated = service.update(id, dto);
        return assembler.toModel(updated);
    }

    @Operation(summary = "Удалить пользователя по ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}