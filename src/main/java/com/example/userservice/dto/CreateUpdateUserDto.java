package com.example.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUpdateUserDto {
    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @Min(0) @Max(150)
    private Integer age;
}