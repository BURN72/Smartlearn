package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.response.ProgressResponse;
import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.Lesson;
import com.smartlearn.demo.entity.Progress;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.repository.EnrollmentRepository;
import com.smartlearn.demo.repository.LessonRepository;
import com.smartlearn.demo.repository.ProgressRepository;
import com.smartlearn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CertificateService certificateService;

    /**
     * Marquer une leçon comme complète
     */
    public ProgressResponse markLessonComplete(Long studentId, Long lessonId, Integer timeSpent) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + studentId));
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée : " + lessonId));

        // Vérifier si l'étudiant a une progression pour cette leçon
        Progress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(Progress.builder()
                        .student(student)
                        .lesson(lesson)
                        .build());

        // Ne marquer comme complète que si ce n'est pas déjà fait
        if (progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }

        if (timeSpent != null && timeSpent > 0) {
            progress.setTimeSpent(timeSpent);
        }

        Progress saved = progressRepository.save(progress);

        // Vérifier la progression du cours et potentiellement générer un certificat
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, lesson.getModule().getCourse().getId())
                .orElse(null);

        if (enrollment != null) {
            Integer courseProgress = calculateCourseProgress(studentId, lesson.getModule().getCourse().getId());
            enrollment.setProgress(courseProgress);
            enrollmentRepository.save(enrollment);

            // Si le cours est à 100%, générer un certificat
            if (courseProgress == 100) {
                certificateService.generateCertificate(enrollment.getId());
            }
        }

        return mapToResponse(saved);
    }

    /**
     * Calculer la progression d'un cours
     */
    public Integer calculateCourseProgress(Long studentId, Long courseId) {
        // Obtenir toutes les leçons du cours
        List<Lesson> allLessons = lessonRepository.findByModuleCourseId(courseId);
        
        if (allLessons.isEmpty()) {
            return 0;
        }

        // Obtenir les leçons complétées par l'étudiant
        List<Progress> completedLessons = progressRepository.findByStudentIdAndLessonModuleCourseIdAndCompletedAtIsNotNull(studentId, courseId);

        int completedCount = completedLessons.size();
        int totalCount = allLessons.size();

        return (completedCount * 100) / totalCount;
    }

    /**
     * Obtenir la progression de l'étudiant dans un cours
     */
    public List<ProgressResponse> getStudentProgress(Long studentId) {
        return progressRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir la progression d'une leçon
     */
    public ProgressResponse getProgressForLesson(Long studentId, Long lessonId) {
        Progress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);

        if (progress == null) {
            return ProgressResponse.builder()
                    .studentId(studentId)
                    .lessonId(lessonId)
                    .isCompleted(false)
                    .build();
        }

        return mapToResponse(progress);
    }

    /**
     * Obtenir les statistiques d'une leçon
     */
    public Integer getCompletionPercentage(Long lessonId) {
        long totalStudents = enrollmentRepository.count();
        long completedStudents = progressRepository.findByLessonIdAndCompletedAtIsNotNull(lessonId).size();

        if (totalStudents == 0) {
            return 0;
        }

        return (int) ((completedStudents * 100) / totalStudents);
    }

    /**
     * Obtenir les leçons complétées d'un étudiant dans un cours
     */
    public List<ProgressResponse> getCourseProgressDetails(Long studentId, Long courseId) {
        return progressRepository.findByStudentIdAndLessonModuleCourseId(studentId, courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ══ Mapper ══

    public ProgressResponse mapToResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .studentId(progress.getStudent().getId())
                .lessonId(progress.getLesson().getId())
                .lessonTitle(progress.getLesson().getTitle())
                .completedAt(progress.getCompletedAt())
                .timeSpent(progress.getTimeSpent())
                .isCompleted(progress.getCompletedAt() != null)
                .build();
    }
}
