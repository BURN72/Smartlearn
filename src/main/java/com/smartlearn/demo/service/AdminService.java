package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.response.CourseAnalyticsResponse;
import com.smartlearn.demo.dto.response.DashboardStatsResponse;
import com.smartlearn.demo.dto.response.EnrollmentAnalyticsResponse;
import com.smartlearn.demo.dto.response.PaymentAnalyticsResponse;
import com.smartlearn.demo.dto.response.UserManagementResponse;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.Payment;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.entity.enums.CourseStatus;
import com.smartlearn.demo.entity.enums.PaymentStatus;
import com.smartlearn.demo.entity.enums.Role;
import com.smartlearn.demo.repository.CertificateRepository;
import com.smartlearn.demo.repository.CourseRepository;
import com.smartlearn.demo.repository.EnrollmentRepository;
import com.smartlearn.demo.repository.LessonRepository;
import com.smartlearn.demo.repository.PaymentRepository;
import com.smartlearn.demo.repository.ProgressRepository;
import com.smartlearn.demo.repository.QuizAttemptRepository;
import com.smartlearn.demo.repository.QuizRepository;
import com.smartlearn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final CertificateRepository certificateRepository;
    private final ProgressRepository progressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;

    // ══ Dashboard Statistics ══

    /**
     * Obtenir les statistiques complètes du dashboard
     */
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_STUDENT)
                .count();
        long totalInstructors = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_INSTRUCTOR)
                .count();
        long totalAdmins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                .count();

        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() == CourseStatus.PUBLIE)
                .count();
        long draftCourses = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() == CourseStatus.BROUILLON)
                .count();
        double averageCoursePrice = courseRepository.findAll().stream()
                .mapToDouble(c -> c.getPrice() != null ? c.getPrice().doubleValue() : 0)
                .average()
                .orElse(0.0);

        long totalEnrollments = enrollmentRepository.count();
        long activeEnrollments = enrollmentRepository.findAll().stream()
                .filter(e -> e.getProgress() != null && e.getProgress() < 100)
                .count();
        long completedEnrollments = enrollmentRepository.findAll().stream()
                .filter(e -> e.getProgress() != null && e.getProgress() >= 100)
                .count();

        List<Payment> allPayments = paymentRepository.findAll();
        long totalTransactions = allPayments.size();
        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCEEDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long successfulPayments = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCEEDED)
                .count();
        long failedPayments = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();

        long totalCertificates = certificateRepository.count();
        long totalQuizAttempts = quizAttemptRepository.count();
        double averageQuizPassRate = quizAttemptRepository.findAll().stream()
                .filter(qa -> qa.getPassed() != null)
                .mapToDouble(qa -> qa.getPassed() ? 1.0 : 0.0)
                .average()
                .orElse(0.0) * 100;

        long totalLessonsCompleted = progressRepository.findAll().stream()
                .filter(p -> p.getCompletedAt() != null)
                .count();
        double averageCourseCompletion = enrollmentRepository.findAll().stream()
                .mapToDouble(e -> e.getProgress() != null ? e.getProgress() : 0)
                .average()
                .orElse(0.0);

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalAdmins(totalAdmins)
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .draftCourses(draftCourses)
                .averageCoursePrice(averageCoursePrice)
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .totalTransactions(totalTransactions)
                .totalRevenue(totalRevenue)
                .successfulPayments(successfulPayments)
                .failedPayments(failedPayments)
                .totalCertificates(totalCertificates)
                .averageQuizPassRate(averageQuizPassRate)
                .totalQuizAttempts(totalQuizAttempts)
                .totalLessonsCompleted(totalLessonsCompleted)
                .averageCourseCompletion(averageCourseCompletion)
                .build();
    }

    // ══ User Management ══

    /**
     * Obtenir tous les utilisateurs
     */
    public List<UserManagementResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les utilisateurs par rôle
     */
    public List<UserManagementResponse> getUsersByRole(String role) {
        Role userRole = Role.valueOf(role);
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == userRole)
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Activer/Désactiver un utilisateur
     */
    public UserManagementResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + userId));
        user.setActive(!user.getActive());
        User updated = userRepository.save(user);
        return mapUserToResponse(updated);
    }


    /**
    * Modifier le rôle d'un utilisateur
    */
    public UserManagementResponse updateUserRole(Long userId, String roleStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + userId));
                
        //Valider le rôle
        Role role;
        try {
                role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
             throw new RuntimeException("Rôle invalide : " + roleStr);
        }
        user.setRole(role);
        userRepository.save(user);
        return mapUserToResponse(user);
        }

    // ══ Course Analytics ══

    /**
     * Obtenir les analytics de tous les cours
     */
    public List<CourseAnalyticsResponse> getAllCourseAnalytics() {
        return courseRepository.findAll().stream()
                .map(this::mapCourseToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les analytics d'un cours spécifique
     */
    public CourseAnalyticsResponse getCourseAnalytics(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        return mapCourseToAnalytics(course);
    }

    /**
     * Obtenir les cours par statut
     */
    public List<CourseAnalyticsResponse> getCoursesByStatus(String status) {
        CourseStatus courseStatus = CourseStatus.valueOf(status);
        return courseRepository.findAll().stream()
                .filter(c -> c.getStatus() == courseStatus)
                .map(this::mapCourseToAnalytics)
                .collect(Collectors.toList());
    }

    // ══ Enrollment Analytics ══

    /**
     * Obtenir tous les enrollments avec analytics
     */
    public List<EnrollmentAnalyticsResponse> getAllEnrollmentAnalytics() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapEnrollmentToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les enrollments par cours
     */
    public List<EnrollmentAnalyticsResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapEnrollmentToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les enrollments d'un étudiant
     */
    public List<EnrollmentAnalyticsResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapEnrollmentToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les enrollments en cours de complétion
     */
    public List<EnrollmentAnalyticsResponse> getIncompleteEnrollments() {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getProgress() != null && e.getProgress() < 100)
                .map(this::mapEnrollmentToAnalytics)
                .collect(Collectors.toList());
    }

    // ══ Payment Analytics ══

    /**
     * Obtenir tous les paiements
     */
    public List<PaymentAnalyticsResponse> getAllPaymentAnalytics() {
        return paymentRepository.findAll().stream()
                .map(this::mapPaymentToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les paiements par statut
     */
    public List<PaymentAnalyticsResponse> getPaymentsByStatus(String status) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        return paymentRepository.findByStatus(paymentStatus).stream()
                .map(this::mapPaymentToAnalytics)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir le revenu total
     */
    public BigDecimal getTotalRevenue() {
        return paymentRepository.findByStatus(PaymentStatus.SUCCEEDED).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtenir le revenu par mois
     */
    public BigDecimal getRevenueByMonth(int month, int year) {
        return paymentRepository.findByStatus(PaymentStatus.SUCCEEDED).stream()
                .filter(p -> p.getCreatedAt().getMonthValue() == month && 
                           p.getCreatedAt().getYear() == year)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ══ Mappers ══

    private UserManagementResponse mapUserToResponse(User user) {
        long coursesCreated = user.getRole() == Role.ROLE_INSTRUCTOR 
                ? courseRepository.findByInstructorId(user.getId()).size()
                : 0;
        
        long coursesEnrolled = user.getRole() == Role.ROLE_STUDENT
                ? enrollmentRepository.findByStudentId(user.getId()).size()
                : 0;

        long studentsEnrolled = user.getRole() == Role.ROLE_INSTRUCTOR
                ? courseRepository.findByInstructorId(user.getId()).stream()
                    .mapToLong(c -> enrollmentRepository.findByCourseId(c.getId()).size())
                    .sum()
                : 0;

        return UserManagementResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .coursesCreated(coursesCreated)
                .studentsEnrolled(studentsEnrolled)
                .coursesEnrolled(coursesEnrolled)
                .build();
    }

    private CourseAnalyticsResponse mapCourseToAnalytics(Course course) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
        long totalEnrollments = enrollments.size();
        long activeEnrollments = enrollments.stream()
                .filter(e -> e.getProgress() != null && e.getProgress() < 100)
                .count();
        long completedEnrollments = enrollments.stream()
                .filter(e -> e.getProgress() != null && e.getProgress() >= 100)
                .count();

        double averageProgress = enrollments.stream()
                .mapToDouble(e -> e.getProgress() != null ? e.getProgress() : 0)
                .average()
                .orElse(0.0);

        long totalLessons = lessonRepository.findByModuleCourseId(course.getId()).size();
        long totalQuizzes = quizRepository.findByModuleCourseId(course.getId()).size();

        double averageQuizPassRate = quizAttemptRepository.findAll().stream()
                .filter(qa -> qa.getQuiz().getModule().getCourse().getId().equals(course.getId()))
                .filter(qa -> qa.getPassed() != null)
                .mapToDouble(qa -> qa.getPassed() ? 1.0 : 0.0)
                .average()
                .orElse(0.0) * 100;

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getEnrollment().getCourse().getId().equals(course.getId()))
                .filter(p -> p.getStatus() == PaymentStatus.SUCCEEDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CourseAnalyticsResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .instructorName(course.getInstructor().getName())
                .status(course.getStatus().name())
                .price(course.getPrice())
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .averageProgress((int) averageProgress)
                .totalLessons(totalLessons)
                .totalQuizzes(totalQuizzes)
                .averageQuizPassRate(averageQuizPassRate)
                .totalRevenue(totalRevenue)
                .build();
    }

    private EnrollmentAnalyticsResponse mapEnrollmentToAnalytics(Enrollment enrollment) {
        long totalLessons = lessonRepository.findByModuleCourseId(enrollment.getCourse().getId()).size();
        long lessonsCompleted = progressRepository.findByStudentIdAndLessonModuleCourseId(
                enrollment.getStudent().getId(), 
                enrollment.getCourse().getId()
        ).stream()
        .filter(p -> p.getCompletedAt() != null)
        .count();

        return EnrollmentAnalyticsResponse.builder()
                .enrollmentId(enrollment.getId())
                .studentName(enrollment.getStudent().getName())
                .studentEmail(enrollment.getStudent().getEmail())
                .courseName(enrollment.getCourse().getTitle())
                .status(enrollment.getStatus().name())
                .progressPercentage(enrollment.getProgress() != null ? enrollment.getProgress() : 0)
                .lessonsCompleted(lessonsCompleted)
                .totalLessons(totalLessons)
                .enrollmentType(enrollment.getCourse().getPrice() != null && 
                               enrollment.getCourse().getPrice().compareTo(BigDecimal.ZERO) > 0 
                               ? "PAID" : "FREE")
                .build();
    }

    private PaymentAnalyticsResponse mapPaymentToAnalytics(Payment payment) {
        return PaymentAnalyticsResponse.builder()
                .transactionId(payment.getId())
                .studentName(payment.getEnrollment().getStudent().getName())
                .courseName(payment.getEnrollment().getCourse().getTitle())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .method(payment.getMethod() != null ? payment.getMethod() : "UNKNOWN")
                .transactionDate(payment.getCreatedAt())
                .refundDate(payment.getRefundedAt())
                .build();
    }
}
