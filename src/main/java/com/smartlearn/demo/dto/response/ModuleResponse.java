package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse {

    private Long id;

    private String title;

    private String description;

    private Integer order;

    private Long courseId;

    private List<LessonResponse> lessons;
}
