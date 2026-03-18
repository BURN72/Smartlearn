package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.SubmitQuizAnswersRequest;
import com.smartlearn.demo.dto.response.AttemptAnswerResponse;
import com.smartlearn.demo.dto.response.QuizAttemptResponse;
import com.smartlearn.demo.entity.AttemptAnswer;
import com.smartlearn.demo.entity.Question;
import com.smartlearn.demo.entity.Quiz;
import com.smartlearn.demo.entity.QuizAnswer;
import com.smartlearn.demo.entity.QuizAttempt;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.entity.enums.QuizAttemptStatus;
import com.smartlearn.demo.repository.AttemptAnswerRepository;
import com.smartlearn.demo.repository.QuestionRepository;
import com.smartlearn.demo.repository.QuizAnswerRepository;
import com.smartlearn.demo.repository.QuizAttemptRepository;
import com.smartlearn.demo.repository.QuizRepository;
import com.smartlearn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    /**
     * Démarrer une tentative de quiz
     */
    public QuizAttemptResponse startAttempt(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé : " + quizId));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + studentId));

        // Véifier que l'utilisateur peut encore tenter le quiz
        long attemptCount = quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                .stream()
                .filter(a -> a.getStatus() != QuizAttemptStatus.EN_COURS)
                .count();

        if (attemptCount >= quiz.getAttempts()) {
            throw new RuntimeException("Nombre maximum de tentatives atteint pour ce quiz");
        }

        // Vérifier qu'il n'y a pas déjà une tentative EN_COURS
        List<QuizAttempt> activeAttempts = quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                .stream()
                .filter(a -> a.getStatus() == QuizAttemptStatus.EN_COURS)
                .collect(Collectors.toList());

        if (!activeAttempts.isEmpty()) {
            throw new RuntimeException("Une tentative est déjà en cours pour ce quiz");
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .status(QuizAttemptStatus.EN_COURS)
                .startedAt(LocalDateTime.now())
                .build();

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        return mapToResponse(saved);
    }

    /**
     * Soumettre les réponses du quiz
     */
    public QuizAttemptResponse submitAnswers(SubmitQuizAnswersRequest request) {
        QuizAttempt attempt = quizAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new RuntimeException("Tentative non trouvée : " + request.getAttemptId()));

        if (attempt.getStatus() != QuizAttemptStatus.EN_COURS) {
            throw new RuntimeException("Cette tentative n'est pas en cours");
        }

        // Vérifier le timeout si le temps limite est défini
        if (attempt.getQuiz().getTimeLimit() != null) {
            LocalDateTime expiredAt = attempt.getStartedAt().plusMinutes(attempt.getQuiz().getTimeLimit());
            if (LocalDateTime.now().isAfter(expiredAt)) {
                attempt.setStatus(QuizAttemptStatus.SOUMIS);
                quizAttemptRepository.save(attempt);
                throw new RuntimeException("Temps limite dépassé");
            }
        }

        // Évaluer chaque réponse
        int totalScore = 0;
        if (request.getAnswers() != null) {
            for (var studentAnswerReq : request.getAnswers()) {
                Question question = questionRepository.findById(studentAnswerReq.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question non trouvée : " + studentAnswerReq.getQuestionId()));

                // Vérifier la réponse et attribuer les points
                boolean isCorrect = evaluateAnswer(question, studentAnswerReq.getAnswer());
                int pointsEarned = isCorrect ? (question.getPoints() != null ? question.getPoints() : 0) : 0;

                AttemptAnswer answer = AttemptAnswer.builder()
                        .attempt(attempt)
                        .question(question)
                        .studentAnswer(studentAnswerReq.getAnswer())
                        .isCorrect(isCorrect)
                        .pointsEarned(pointsEarned)
                        .build();

                attemptAnswerRepository.save(answer);
                totalScore += pointsEarned;
            }
        }

        // Calculer la note sur 100 et déterminer le statut
        int maxPoints = attempt.getQuiz().getQuestions().stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 0)
                .sum();

        int scorePercentage = maxPoints > 0 ? (totalScore * 100) / maxPoints : 0;

        attempt.setScore(scorePercentage);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setPassed(scorePercentage >= attempt.getQuiz().getPassMark());
        attempt.setStatus(scorePercentage >= attempt.getQuiz().getPassMark() ? QuizAttemptStatus.RÉUSSI : QuizAttemptStatus.ÉCHOUÉ);

        QuizAttempt updated = quizAttemptRepository.save(attempt);
        return mapToResponse(updated);
    }

    /**
     * Obtenir les détails d'une tentative
     */
    public QuizAttemptResponse getAttemptDetails(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Tentative non trouvée : " + attemptId));
        return mapToResponse(attempt);
    }

    /**
     * Obtenir toutes les tentatives d'un étudiant
     */
    public List<QuizAttemptResponse> getAttemptsByStudent(Long studentId) {
        return quizAttemptRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les tentatives d'un étudiant pour un quiz spécifique
     */
    public List<QuizAttemptResponse> getAttemptsByStudentAndQuiz(Long studentId, Long quizId) {
        return quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si un étudiant peut encore tenter le quiz
     */
    public boolean canAttempt(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé : " + quizId));

        long attemptCount = quizAttemptRepository.findByStudentIdAndQuizId(studentId, quizId)
                .stream()
                .filter(a -> a.getStatus() != QuizAttemptStatus.EN_COURS)
                .count();

        return attemptCount < quiz.getAttempts();
    }

    // ══ Helpers ══

    /**
     * Évaluer la réponse d'un étudiant
     */
    private boolean evaluateAnswer(Question question, String studentAnswer) {
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            return false;
        }

        // Si c'est une question à réponse courte, comparer directement
        if (question.getCorrectAnswer() != null) {
            return studentAnswer.equalsIgnoreCase(question.getCorrectAnswer().trim());
        }

        // Chercher parmi les réponses possibles (MCQ)
        List<QuizAnswer> validAnswers = quizAnswerRepository.findByQuestionId(question.getId())
                .stream()
                .filter(QuizAnswer::getIsCorrect)
                .collect(Collectors.toList());
        
        return validAnswers.stream()
                .anyMatch(qa -> qa.getAnswerText().equalsIgnoreCase(studentAnswer.trim()));
    }

    // ══ Mapper ══

    public QuizAttemptResponse mapToResponse(QuizAttempt attempt) {
        List<AttemptAnswerResponse> answers = attemptAnswerRepository.findByAttemptId(attempt.getId())
                .stream()
                .map(this::mapAnswerToResponse)
                .collect(Collectors.toList());

        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .studentId(attempt.getStudent().getId())
                .studentName(attempt.getStudent().getUsername())
                .score(attempt.getScore())
                .status(attempt.getStatus())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .passed(attempt.getStatus() == QuizAttemptStatus.RÉUSSI)
                .answers(answers)
                .build();
    }

    private AttemptAnswerResponse mapAnswerToResponse(AttemptAnswer answer) {
        Question question = answer.getQuestion();
        Integer totalPoints = question.getPoints();

        return AttemptAnswerResponse.builder()
                .id(answer.getId())
                .studentAnswer(answer.getStudentAnswer())
                .isCorrect(answer.getIsCorrect())
                .pointsEarned(answer.getPointsEarned())
                .questionId(question.getId())
                .questionText(question.getText())
                .totalPoints(totalPoints)
                .build();
    }
}
