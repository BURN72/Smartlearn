package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;

    private String title;

    private String description;

    private Integer timeLimit;

    private Integer passMark;

    private Integer attempts;

    private Long courseId;

    private String courseName;
}
