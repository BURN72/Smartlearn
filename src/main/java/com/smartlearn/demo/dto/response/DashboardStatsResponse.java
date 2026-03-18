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
public class DashboardStatsResponse {

    // Utilisateurs
    private Long totalUsers;
    private Long totalStudents;
    private Long totalInstructors;
    private Long totalAdmins;

    // Cours
    private Long totalCourses;
    private Long publishedCourses;
    private Long draftCourses;
    private Double averageCoursePrice;

    // Inscriptions
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;

    // Paiements
    private Long totalTransactions;
    private BigDecimal totalRevenue;
    private Long successfulPayments;
    private Long failedPayments;

    // Quiz & Certificats
    private Long totalCertificates;
    private Double averageQuizPassRate;
    private Long totalQuizAttempts;

    // Engagement
    private Double averageCourseCompletion;
    private Long totalLessonsCompleted;
}
