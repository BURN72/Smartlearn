package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.request.SubmitQuizAnswersRequest;
import com.smartlearn.demo.dto.response.QuizAttemptResponse;
import com.smartlearn.demo.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    /**
     * Démarrer une tentative de quiz
     */
    @PostMapping("/start/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<QuizAttemptResponse> startAttempt(@PathVariable Long quizId, Authentication auth) {
        // Obtenir l'ID de l'utilisateur connecté depuis le JWT
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizAttemptService.startAttempt(quizId, studentId));
    }

    // ══ ROUTES SPÉCIFIQUES (AVANT LES ROUTES GÉNÉRIQUES) ══

    /**
     * Obtenir toutes les tentatives de l'étudiant connecté
     */
    @GetMapping("/my-attempts")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<QuizAttemptResponse>> getMyAttempts(Authentication auth) {
        Long studentId = extractUserIdFromAuth(auth);
        return ResponseEntity.ok(quizAttemptService.getAttemptsByStudent(studentId));
    }

    /**
     * Obtenir les tentatives d'un étudiant pour un cours (admin/instructeur)
     */
    @GetMapping("/quiz/{quizId}/student/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<QuizAttemptResponse>> getAttemptsByStudentAndQuiz(@PathVariable Long quizId, @PathVariable Long studentId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptsByStudentAndQuiz(studentId, quizId));
    }

    /**
     * Vérifier si un étudiant peut tenter le quiz
     */
    @GetMapping("/{quizId}/can-attempt/{studentId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<Boolean> canAttempt(@PathVariable Long quizId, @PathVariable Long studentId) {
        return ResponseEntity.ok(quizAttemptService.canAttempt(quizId, studentId));
    }

    // ══ ROUTES GÉNÉRIQUES (APRÈS LES ROUTES SPÉCIFIQUES) ══

    /**
     * Soumettre les réponses du quiz
     */
    @PostMapping("/{attemptId}/submit")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<QuizAttemptResponse> submitAnswers(@PathVariable Long attemptId, @RequestBody SubmitQuizAnswersRequest request) {
        request.setAttemptId(attemptId);
        return ResponseEntity.ok(quizAttemptService.submitAnswers(request));
    }

    /**
     * Obtenir les détails d'une tentative
     */
    @GetMapping("/{attemptId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<QuizAttemptResponse> getAttemptDetails(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizAttemptService.getAttemptDetails(attemptId));
    }

    // ══ Helpers ══

    private Long extractUserIdFromAuth(Authentication auth) {
        // À adapter selon votre implémentation de SecurityConfig
        // Pour l'instant, retourner un placeholder
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }
        // À compléter selon votre entité User
        return null;
    }
}
