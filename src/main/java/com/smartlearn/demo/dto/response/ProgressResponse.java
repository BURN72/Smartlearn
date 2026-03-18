package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private Long id;

    private LocalDateTime completedAt;

    private Integer timeSpent; // minutes

    private Long studentId;

    private Long lessonId;

    private String lessonTitle;

    private Boolean isCompleted;
}
