package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEnrollmentRequest {

    @NotNull(message = "L'ID du cours est obligatoire")
    private Long courseId;
}
