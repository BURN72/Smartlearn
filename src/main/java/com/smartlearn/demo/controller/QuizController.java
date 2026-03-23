package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.request.CreateQuestionRequest;
import com.smartlearn.demo.dto.request.CreateQuizRequest;
import com.smartlearn.demo.dto.response.QuestionResponse;
import com.smartlearn.demo.dto.response.QuizDetailResponse;
import com.smartlearn.demo.dto.response.QuizResponse;
import com.smartlearn.demo.service.QuestionService;
import com.smartlearn.demo.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;

    /**
     * Créer un nouveau quiz
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody CreateQuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request));
    }

    // ══ ROUTES SPÉCIFIQUES (AVANT LES ROUTES GÉNÉRIQUES) ══

    /**
     * Obtenir les quiz d'un module
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizResponse>> getQuizzesByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(quizService.getQuizzesByModule(moduleId));
    }

    // ══ ROUTES GÉNÉRIQUES (APRÈS LES ROUTES SPÉCIFIQUES) ══

    /**
     * Obtenir un quiz avec ses questions
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    /**
     * Mettre à jour un quiz
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, @RequestBody CreateQuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    /**
     * Supprimer un quiz
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ajouter une question à un quiz
     */
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<QuestionResponse> createQuestion(@PathVariable Long quizId, @RequestBody CreateQuestionRequest request) {
        request.setQuizId(quizId);
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
    }

    /**
     * Obtenir une question
     */
    @GetMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.getQuestionById(questionId));
    }

    /**
     * Supprimer une question
     */
    @DeleteMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
