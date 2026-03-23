package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateQuizRequest;
import com.smartlearn.demo.dto.response.QuestionResponse;
import com.smartlearn.demo.dto.response.QuizDetailResponse;
import com.smartlearn.demo.dto.response.QuizResponse;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Quiz;
import com.smartlearn.demo.entity.Module;
import com.smartlearn.demo.repository.ModuleRepository;
import com.smartlearn.demo.repository.CourseRepository;
import com.smartlearn.demo.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionService questionService;

    /**
     * Créer un quiz pour un cours
     */
    public QuizResponse createQuiz(CreateQuizRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module non trouvé : " + request.getModuleId()));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimit(request.getTimeLimit())
                .passMark(request.getPassMark())
                .attempts(request.getAttempts() != null ? request.getAttempts() : 3)
                .module(module)
                .build();

        Quiz saved = quizRepository.save(quiz);
        return mapToResponse(saved);
    }

    /**
     * Obtenir un quiz avec ses questions
     */
    public QuizDetailResponse getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé : " + quizId));

        List<QuestionResponse> questions = questionService.getQuestionsByQuiz(quizId);
        
        return QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .passMark(quiz.getPassMark())
                .attempts(quiz.getAttempts())
                .courseId(quiz.getModule().getCourse().getId())
                .courseName(quiz.getModule().getCourse().getTitle())
                .questions(questions)
                .totalPoints(calculateTotalPoints(quizId))
                .build();
    }

    /**
     * Obtenir les quiz d'un module
     */
    public List<QuizResponse> getQuizzesByModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new RuntimeException("Module non trouvé : " + moduleId);
        }

        return quizRepository.findByModuleId(moduleId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour un quiz
     */
    public QuizResponse updateQuiz(Long quizId, CreateQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé : " + quizId));

        if (request.getTitle() != null) quiz.setTitle(request.getTitle());
        if (request.getDescription() != null) quiz.setDescription(request.getDescription());
        if (request.getTimeLimit() != null) quiz.setTimeLimit(request.getTimeLimit());
        if (request.getPassMark() != null) quiz.setPassMark(request.getPassMark());
        if (request.getAttempts() != null) quiz.setAttempts(request.getAttempts());

        Quiz updated = quizRepository.save(quiz);
        return mapToResponse(updated);
    }

    /**
     * Supprimer un quiz
     */
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz non trouvé : " + quizId);
        }
        quizRepository.deleteById(quizId);
    }

    /**
     * Calculer le nombre total de points pour un quiz
     */
    public Integer calculateTotalPoints(Long quizId) {
        return quizRepository.findById(quizId)
                .map(quiz -> quiz.getQuestions().stream()
                        .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 0)
                        .sum())
                .orElse(0);
    }

    /**
     * Vérifier si un quiz existe
     */
    public boolean existsById(Long quizId) {
        return quizRepository.existsById(quizId);
    }

    // ══ Mapper ══

    public QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .passMark(quiz.getPassMark())
                .attempts(quiz.getAttempts())
                .courseId(quiz.getModule().getCourse().getId())
                .courseName(quiz.getModule().getCourse().getTitle())
                .build();
    }
}
