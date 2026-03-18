package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerResponse {

    private Long id;

    private String studentAnswer;

    private Boolean isCorrect;

    private Integer pointsEarned;

    private Long questionId;

    private String questionText;

    private Integer totalPoints;
}
