package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuizRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    private Integer timeLimit; // minutes

    @NotNull(message = "La note de passage est obligatoire")
    private Integer passMark; // en %

    @NotNull(message = "Le nombre maximum de tentatives est obligatoire")
    private Integer attempts;

    @NotNull(message = "L'ID du cours est obligatoire")
    private Long courseId;
}
