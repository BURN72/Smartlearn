package com.smartlearn.demo.dto.request;

import lombok.Data;

@Data
public class StudentAnswerRequest {

    private Long questionId;

    private String answer;
}
