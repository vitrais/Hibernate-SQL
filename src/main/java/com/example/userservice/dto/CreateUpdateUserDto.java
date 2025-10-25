package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(name = "CreateUpdateUser", description = "Модель создания/обновления пользователя")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUpdateUserDto {

    @Schema(description = "Имя пользователя", example = "Viktor")
    @NotBlank
    @Size(max = 100)
    private String name;

    @Schema(description = "Email пользователя", example = "viktor@example.com")
    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Schema(description = "Возраст пользователя", example = "25", minimum = "0", maximum = "150")
    @Min(0)
    @Max(150)
    private Integer age;
}