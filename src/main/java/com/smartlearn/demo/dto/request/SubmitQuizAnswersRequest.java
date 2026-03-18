package com.smartlearn.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitQuizAnswersRequest {

    private Long attemptId;

    private List<StudentAnswerRequest> answers;
}
