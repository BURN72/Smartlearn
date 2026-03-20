package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @PositiveOrZero(message = "Le prix doit être positif ou zero, Jamais negatif")
    private BigDecimal price;

    @NotBlank(message = "Le niveau est obligatoire")
    private String level;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;
}
