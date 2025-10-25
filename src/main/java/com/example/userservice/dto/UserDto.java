package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(name = "User", description = "Пользователь доменной модели")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Viktor")
    private String name;

    @Schema(description = "Email пользователя", example = "viktor@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "25")
    private Integer age;

    @Schema(description = "Дата и время создания", example = "2025-10-01T12:00:00")
    private LocalDateTime createdAt;
}