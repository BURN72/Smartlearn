package com.smartlearn.demo.dto.response;

import com.smartlearn.demo.entity.enums.QuizAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {

    private Long id;

    private Integer score;

    private QuizAttemptStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Boolean passed;

    private Long studentId;

    private String studentName;

    private Long quizId;

    private String quizTitle;

    private List<AttemptAnswerResponse> answers;
}
