package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateModuleRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @NotNull(message = "L'ordre est obligatoire")
    private Integer order;

    @NotNull(message = "L'ID du cours est obligatoire")
    private Long courseId;
}
