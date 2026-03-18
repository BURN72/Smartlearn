package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.response.CertificateResponse;
import com.smartlearn.demo.entity.Certificate;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.repository.CertificateRepository;
import com.smartlearn.demo.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Générer un certificat automatiquement
     */
    public CertificateResponse generateCertificate(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        // Vérifier que la progression est à 100%
        if (enrollment.getProgress() != null && enrollment.getProgress() >= 100) {
            // Vérifier s'il y a déjà un certificat
            List<Certificate> existing = certificateRepository.findByStudentIdAndCourseId(enrollment.getStudent().getId(), enrollment.getCourse().getId());
            if (!existing.isEmpty()) {
                return mapToResponse(existing.get(0));
            }

            // Générer un certificat unique
            Certificate certificate = Certificate.builder()
                    .student(enrollment.getStudent())
                    .course(enrollment.getCourse())
                    .uniqueCode(generateUniqueCode())
                    .issuedAt(LocalDateTime.now())
                    .certificateUrl(generateCertificateUrl(enrollment))
                    .build();

            Certificate saved = certificateRepository.save(certificate);
            return mapToResponse(saved);
        }

        throw new RuntimeException("L'étudiant n'a pas complété 100% du cours");
    }

    /**
     * Obtenir un certificat par son code unique
     */
    public CertificateResponse getCertificateByCode(String uniqueCode) {
        Certificate certificate = certificateRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new RuntimeException("Certificat non trouvé : " + uniqueCode));
        return mapToResponse(certificate);
    }

    /**
     * Obtenir les certificats d'un étudiant
     */
    public List<CertificateResponse> getCertificatesByStudent(Long studentId) {
        return certificateRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les certificats d'un cours
     */
    public List<CertificateResponse> getCertificatesByCourse(Long courseId) {
        return certificateRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si un étudiant possède un certificat pour un cours
     */
    public boolean hasCertificate(Long studentId, Long courseId) {
        return !certificateRepository.findByStudentIdAndCourseId(studentId, courseId).isEmpty();
    }

    // ══ Helpers ══

    /**
     * Générer un code unique pour le certificat
     */
    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    /**
     * Générer l'URL du certificat
     */
    private String generateCertificateUrl(Enrollment enrollment) {
        // Format: /api/certificates/{uniqueCode}
        // À adapter selon votre infrastructure
        return "/certificates/" + generateUniqueCode();
    }

    // ══ Mapper ══

    public CertificateResponse mapToResponse(Certificate certificate) {
        User student = certificate.getStudent();
        Course course = certificate.getCourse();
        User instructor = course.getInstructor();

        return CertificateResponse.builder()
                .id(certificate.getId())
                .uniqueCode(certificate.getUniqueCode())
                .issuedAt(certificate.getIssuedAt())
                .certificateUrl(certificate.getCertificateUrl())
                .studentId(student.getId())
                .studentName(student.getUsername())
                .courseId(course.getId())
                .courseName(course.getTitle())
                .instructorName(instructor.getUsername())
                .build();
    }
}
