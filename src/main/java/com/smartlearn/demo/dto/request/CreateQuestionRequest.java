package com.smartlearn.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {

    private String text;

    private String type; // MCQ, TRUE_FALSE, SHORT_ANSWER

    private Integer points;

    private String correctAnswer;

    private Long quizId;

    // Pour les réponses multiples choix
    private List<CreateQuizAnswerRequest> answers;
}
