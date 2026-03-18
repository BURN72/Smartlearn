package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateEnrollmentRequest;
import com.smartlearn.demo.dto.response.EnrollmentResponse;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.entity.enums.EnrollmentStatus;
import com.smartlearn.demo.repository.CourseRepository;
import com.smartlearn.demo.repository.EnrollmentRepository;
import com.smartlearn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Créer une inscription (EN_ATTENTE pour cours payant, ACTIF pour gratuit)
     */
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer le cours
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + request.getCourseId()));

        // Vérifier si déjà inscrit
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new RuntimeException("Vous êtes déjà inscrit à ce cours");
        }

        // Déterminer le statut en fonction du prix
        EnrollmentStatus status = (course.getPrice() == null || course.getPrice().compareTo(BigDecimal.ZERO) <= 0)
                ? EnrollmentStatus.ACTIF
                : EnrollmentStatus.EN_ATTENTE;

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(status)
                .progress(0)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Envoyer email de confirmation pour cours gratuit
        if (status == EnrollmentStatus.ACTIF) {
            emailService.sendEnrollmentConfirmation(
                    student.getEmail(),
                    student.getName(),
                    course.getTitle()
            );
        }

        return mapToResponse(saved);
    }

    /**
     * Récupérer une inscription
     */
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));
        return mapToResponse(enrollment);
    }

    /**
     * Récupérer les inscriptions d'un étudiant
     */
    public List<EnrollmentResponse> getMyEnrollments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return enrollmentRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les inscriptions d'un cours
     */
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Activer une inscription (après paiement)
     */
    public EnrollmentResponse activateEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.ACTIF);
        Enrollment updated = enrollmentRepository.save(enrollment);

        // Envoyer email de confirmation de paiement
        emailService.sendEnrollmentConfirmation(
                enrollment.getStudent().getEmail(),
                enrollment.getStudent().getName(),
                enrollment.getCourse().getTitle()
        );

        return mapToResponse(updated);
    }

    /**
     * Mettre à jour la progression
     */
    public EnrollmentResponse updateProgress(Long enrollmentId, Integer progressPercentage) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        enrollment.setProgress(progressPercentage);

        // Si 100%, marquer comme TERMINÉ
        if (progressPercentage >= 100) {
            enrollment.setStatus(EnrollmentStatus.TERMINÉ);
        }

        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    /**
     * Annuler une inscription
     */
    public EnrollmentResponse cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.ANNULÉ);
        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    /**
     * Rembourser une inscription
     */
    public EnrollmentResponse refundEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.REMBOURSÉ);
        Enrollment updated = enrollmentRepository.save(enrollment);

        // Envoyer email de confirmation de remboursement
        emailService.sendRefundConfirmation(
                enrollment.getStudent().getEmail(),
                enrollment.getStudent().getName(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getPrice().toString(),
                "XAF"
        );

        return mapToResponse(updated);
    }

    /**
     * Vérifier si un étudiant est inscrit à un cours
     */
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * Mapper une entité Enrollment vers EnrollmentResponse
     */
    public EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .progress(enrollment.getProgress())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getName())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getTitle())
                .isPaid(enrollment.getStatus() == EnrollmentStatus.ACTIF 
                        || enrollment.getStatus() == EnrollmentStatus.TERMINÉ)
                .build();
    }
}
