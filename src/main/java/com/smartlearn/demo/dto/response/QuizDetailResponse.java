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
public class QuizDetailResponse {

    private Long id;

    private String title;

    private String description;

    private Integer timeLimit;

    private Integer passMark;

    private Integer attempts;

    private Long courseId;

    private String courseName;

    private List<QuestionResponse> questions;

    private Integer totalPoints;
}
