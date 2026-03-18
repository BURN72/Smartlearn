package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.response.CertificateResponse;
import com.smartlearn.demo.dto.response.ProgressResponse;
import com.smartlearn.demo.service.CertificateService;
import com.smartlearn.demo.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final CertificateService certificateService;

    /**
     * Marquer une leçon comme complète
     */
    @PostMapping("/mark-complete/{lessonId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ProgressResponse> markLessonComplete(@PathVariable Long lessonId, @RequestParam(required = false) Integer timeSpent, Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(progressService.markLessonComplete(studentId, lessonId, timeSpent));
    }

    /**
     * Obtenir la progression du cours
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Integer> getCourseProgress(@PathVariable Long courseId, Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(progressService.calculateCourseProgress(studentId, courseId));
    }

    /**
     * Obtenir les détails de progression d'un cours
     */
    @GetMapping("/course/{courseId}/details")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<ProgressResponse>> getCourseProgressDetails(@PathVariable Long courseId, Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(progressService.getCourseProgressDetails(studentId, courseId));
    }

    /**
     * Obtenir toute la progression de l'étudiant
     */
    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<ProgressResponse>> getMyProgress(Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(progressService.getStudentProgress(studentId));
    }

    /**
     * Obtenir la progression pour une leçon spécifique
     */
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ProgressResponse> getProgressForLesson(@PathVariable Long lessonId, Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(progressService.getProgressForLesson(studentId, lessonId));
    }

    /**
     * Obtenir le taux de complétude d'une leçon
     */
    @GetMapping("/lesson/{lessonId}/completion-rate")
    public ResponseEntity<Integer> getLessonCompletionRate(@PathVariable Long lessonId) {
        return ResponseEntity.ok(progressService.getCompletionPercentage(lessonId));
    }

    // ══ Certificate Endpoints ══

    /**
     * Obtenir les certificats de l'étudiant
     */
    @GetMapping("/certificates/my-certificates")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<CertificateResponse>> getMyCertificates(Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(certificateService.getCertificatesByStudent(studentId));
    }

    /**
     * Obtenir un certificat par son code unique
     */
    @GetMapping("/certificates/{code}")
    public ResponseEntity<CertificateResponse> getCertificateByCode(@PathVariable String code) {
        return ResponseEntity.ok(certificateService.getCertificateByCode(code));
    }

    /**
     * Vérifier si l'étudiant a un certificat pour un cours
     */
    @GetMapping("/certificates/verify/{courseId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Boolean> hasCertificate(@PathVariable Long courseId, Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(certificateService.hasCertificate(studentId, courseId));
    }

    /**
     * Obtenir les certificats d'un cours (admin/instructeur)
     */
    @GetMapping("/certificates/course/{courseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<CertificateResponse>> getCertificatesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(certificateService.getCertificatesByCourse(courseId));
    }

    // ══ Helpers ══

    private Long extractUserIdFromAuth(Authentication auth) {
        // À adapter selon votre implémentation de SecurityConfig
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }
        return null;
    }
}
