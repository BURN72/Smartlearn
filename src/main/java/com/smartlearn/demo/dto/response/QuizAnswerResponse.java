package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponse {

    private Long id;

    private String answerText;

    private Boolean isCorrect;

    private Integer order;

    private Long questionId;
}
