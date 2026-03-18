package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCourseRequest {

    private String title;

    private String description;

    @Positive(message = "Le prix doit être positif")
    private BigDecimal price;

    private String level;

    private Long categoryId;

    private String status;
}
