package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentAnalyticsResponse {

    private Long enrollmentId;
    private String studentName;
    private String studentEmail;
    private String courseName;
    private String status;
    private Integer progressPercentage;
    private Long lessonsCompleted;
    private Long totalLessons;
    private String enrollmentType; // FREE or PAID
    private Double estimatedCompletion; // jours restants estimés
}
