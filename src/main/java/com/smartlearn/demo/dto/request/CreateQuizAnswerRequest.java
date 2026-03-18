package com.smartlearn.demo.dto.request;

import lombok.Data;

@Data
public class CreateQuizAnswerRequest {

    private String answerText;

    private Boolean isCorrect;

    private Integer order;
}
