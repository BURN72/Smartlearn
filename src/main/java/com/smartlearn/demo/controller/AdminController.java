package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.response.CourseAnalyticsResponse;
import com.smartlearn.demo.dto.response.DashboardStatsResponse;
import com.smartlearn.demo.dto.response.EnrollmentAnalyticsResponse;
import com.smartlearn.demo.dto.response.PaymentAnalyticsResponse;
import com.smartlearn.demo.dto.response.UserManagementResponse;
import com.smartlearn.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ══ Dashboard ══

    /**
     * Obtenir les statistiques complètes du dashboard
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ══ User Management ══

    /**
     * Obtenir tous les utilisateurs
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserManagementResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Obtenir les utilisateurs par rôle
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserManagementResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }

    /**
     * Activer/Désactiver un utilisateur
     */
    @PostMapping("/users/{userId}/toggle-status")
    public ResponseEntity<UserManagementResponse> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleUserStatus(userId));
    }

    // ══ Course Analytics ══

    /**
     * Obtenir les analytics de tous les cours
     */
    @GetMapping("/courses/analytics")
    public ResponseEntity<List<CourseAnalyticsResponse>> getAllCourseAnalytics() {
        return ResponseEntity.ok(adminService.getAllCourseAnalytics());
    }

    /**
     * Obtenir les analytics d'un cours spécifique
     */
    @GetMapping("/courses/{courseId}/analytics")
    public ResponseEntity<CourseAnalyticsResponse> getCourseAnalytics(@PathVariable Long courseId) {
        return ResponseEntity.ok(adminService.getCourseAnalytics(courseId));
    }

    /**
     * Obtenir les cours par statut
     */
    @GetMapping("/courses/status/{status}")
    public ResponseEntity<List<CourseAnalyticsResponse>> getCoursesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminService.getCoursesByStatus(status));
    }

    // ══ Enrollment Analytics ══

    /**
     * Obtenir tous les enrollments
     */
    @GetMapping("/enrollments/analytics")
    public ResponseEntity<List<EnrollmentAnalyticsResponse>> getAllEnrollmentAnalytics() {
        return ResponseEntity.ok(adminService.getAllEnrollmentAnalytics());
    }

    /**
     * Obtenir les enrollments par cours
     */
    @GetMapping("/enrollments/course/{courseId}")
    public ResponseEntity<List<EnrollmentAnalyticsResponse>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(adminService.getEnrollmentsByCourse(courseId));
    }

    /**
     * Obtenir les enrollments d'un étudiant
     */
    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<EnrollmentAnalyticsResponse>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(adminService.getEnrollmentsByStudent(studentId));
    }

    /**
     * Obtenir les enrollments incomplets
     */
    @GetMapping("/enrollments/incomplete")
    public ResponseEntity<List<EnrollmentAnalyticsResponse>> getIncompleteEnrollments() {
        return ResponseEntity.ok(adminService.getIncompleteEnrollments());
    }

    // ══ Payment Analytics ══

    /**
     * Obtenir tous les paiements
     */
    @GetMapping("/payments/analytics")
    public ResponseEntity<List<PaymentAnalyticsResponse>> getAllPaymentAnalytics() {
        return ResponseEntity.ok(adminService.getAllPaymentAnalytics());
    }

    /**
     * Obtenir les paiements par statut
     */
    @GetMapping("/payments/status/{status}")
    public ResponseEntity<List<PaymentAnalyticsResponse>> getPaymentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminService.getPaymentsByStatus(status));
    }

    /**
     * Obtenir le revenu total
     */
    @GetMapping("/payments/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(adminService.getTotalRevenue());
    }

    /**
     * Obtenir le revenu par mois
     */
    @GetMapping("/payments/revenue/monthly")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(adminService.getRevenueByMonth(month, year));
    }
}
