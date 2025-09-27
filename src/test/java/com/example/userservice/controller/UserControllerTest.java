package com.example.userservice.controller;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserService service;

    @Test
    @DisplayName("GET /users -> список DTO")
    void list_ok() throws Exception {
        when(service.list()).thenReturn(List.of(
                UserDto.builder().id(1L).name("A").email("a@x.com").age(20).createdAt(LocalDateTime.now()).build()
        ));

        mvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("a@x.com"));
    }

    @Test
    @DisplayName("POST /users -> 201 + Location")
    void create_ok() throws Exception {
        var req = CreateUpdateUserDto.builder().name("Alice").email("alice@example.com").age(25).build();
        var res = UserDto.builder().id(10L).name("Alice").email("alice@example.com").age(25).createdAt(LocalDateTime.now()).build();
        when(service.create(any())).thenReturn(res);

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/10"))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("PUT /users/{id} -> 200")
    void update_ok() throws Exception {
        var req = CreateUpdateUserDto.builder().name("Bob").email("bob@example.com").age(30).build();
        var res = UserDto.builder().id(1L).name("Bob").email("bob@example.com").age(30).createdAt(LocalDateTime.now()).build();
        when(service.update(eq(1L), any())).thenReturn(res);

        mvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    @DisplayName("DELETE /users/{id} -> 204")
    void delete_ok() throws Exception {
        mvc.perform(delete("/api/v1/users/5"))
                .andExpect(status().isNoContent());
        verify(service).delete(5L);
    }

    @Test
    @DisplayName("Валидация запроса -> 400")
    void validation_fail() throws Exception {
        var bad = CreateUpdateUserDto.builder().name("").email("bad").age(-1).build();
        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }
}