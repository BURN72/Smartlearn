package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAnalyticsResponse {

    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private String status;
    private BigDecimal price;
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    private Integer averageProgress;
    private Long totalLessons;
    private Long totalQuizzes;
    private Double averageQuizPassRate;
    private BigDecimal totalRevenue;
}
