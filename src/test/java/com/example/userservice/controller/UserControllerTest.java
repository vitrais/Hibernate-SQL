package com.example.userservice.controller;

import com.example.userservice.dto.CreateUpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository repo;

    @BeforeEach
    void cleanDb() { repo.deleteAll(); }

    @Test @Order(1)
    void create_returns201_and_persistsToDb() throws Exception {
        var dto = CreateUpdateUserDto.builder()
                .name("Alice").email("alice@example.com").age(25).build();

        var res = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andReturn();

        assertEquals(201, res.getResponse().getStatus());

        var body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var created = om.readValue(body, UserDto.class);

        assertNotNull(created.getId());
        assertEquals("alice@example.com", created.getEmail());

        assertEquals(1, repo.count());
        User saved = repo.findById(created.getId()).orElseThrow();
        assertEquals("Alice", saved.getName());
        assertEquals(25, saved.getAge());
    }

    @Test @Order(2)
    void list_returnsAll_fromDb() throws Exception {
        repo.save(User.builder().name("A").email("a@x.com").age(20).build());
        repo.save(User.builder().name("B").email("b@x.com").age(30).build());

        var res = mvc.perform(get("/api/v1/users")).andReturn();
        assertEquals(200, res.getResponse().getStatus());

        var body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<UserDto> list = om.readValue(body, new TypeReference<>() {});
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(u -> "a@x.com".equals(u.getEmail())));
        assertTrue(list.stream().anyMatch(u -> "b@x.com".equals(u.getEmail())));
    }

    @Test @Order(3)
    void get_byId_returnsFromDb() throws Exception {
        var u = repo.save(User.builder().name("Bob").email("bob@example.com").age(31).build());

        var res = mvc.perform(get("/api/v1/users/{id}", u.getId())).andReturn();
        assertEquals(200, res.getResponse().getStatus());

        var body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var dto = om.readValue(body, UserDto.class);
        assertEquals(u.getId(), dto.getId());
        assertEquals("bob@example.com", dto.getEmail());
    }

    @Test @Order(4)
    void update_modifiesRowInDb() throws Exception {
        var u = repo.save(User.builder().name("X").email("x@x.com").age(10).build());
        var upd = CreateUpdateUserDto.builder().name("X updated").email("x@x.com").age(11).build();

        var res = mvc.perform(put("/api/v1/users/{id}", u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(upd)))
                .andReturn();
        assertEquals(200, res.getResponse().getStatus());

        var body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var dto = om.readValue(body, UserDto.class);
        assertEquals("X updated", dto.getName());
        assertEquals(11, dto.getAge());

        var fromDb = repo.findById(u.getId()).orElseThrow();
        assertEquals("X updated", fromDb.getName());
        assertEquals(11, fromDb.getAge());
    }

    @Test @Order(5)
    void delete_removesRowFromDb() throws Exception {
        var u = repo.save(User.builder().name("C").email("c@x.com").age(40).build());

        var res = mvc.perform(delete("/api/v1/users/{id}", u.getId())).andReturn();
        assertEquals(204, res.getResponse().getStatus());

        assertFalse(repo.findById(u.getId()).isPresent());
        assertEquals(0, repo.count());
    }

    @Test @Order(6)
    void create_withInvalidBody_returns400_andNoInsert() throws Exception {
        var bad = CreateUpdateUserDto.builder().name("").email("bad").age(-1).build();

        var res = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bad)))
                .andReturn();

        assertEquals(400, res.getResponse().getStatus());

        var body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Map<String,String> errors = om.readValue(body, new TypeReference<>() {});
        assertTrue(errors.containsKey("name"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("age"));

        assertEquals(0, repo.count());
    }
}