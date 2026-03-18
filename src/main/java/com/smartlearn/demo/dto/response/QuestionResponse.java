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
public class QuestionResponse {

    private Long id;

    private String text;

    private String type;

    private Integer points;

    private String correctAnswer;

    private Long quizId;

    private List<QuizAnswerResponse> answers;
}
