package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLessonRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le type est obligatoire")
    private String type;

    private String content;

    private Integer duration;

    @NotNull(message = "L'ordre est obligatoire")
    private Integer orderIndex;

    @NotNull(message = "L'ID du module est obligatoire")
    private Long moduleId;

    private Boolean isFree;
}
