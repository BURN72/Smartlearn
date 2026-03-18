package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateQuestionRequest;
import com.smartlearn.demo.dto.request.CreateQuizAnswerRequest;
import com.smartlearn.demo.dto.response.QuestionResponse;
import com.smartlearn.demo.dto.response.QuizAnswerResponse;
import com.smartlearn.demo.entity.Question;
import com.smartlearn.demo.entity.Quiz;
import com.smartlearn.demo.entity.QuizAnswer;
import com.smartlearn.demo.repository.QuestionRepository;
import com.smartlearn.demo.repository.QuizAnswerRepository;
import com.smartlearn.demo.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    /**
     * Créer une question
     */
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé : " + request.getQuizId()));

        Question question = Question.builder()
                .text(request.getText())
                .type(request.getType())
                .points(request.getPoints())
                .correctAnswer(request.getCorrectAnswer())
                .quiz(quiz)
                .build();

        Question saved = questionRepository.save(question);

        // Enregistrer les réponses possibles si MCQ
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            for (CreateQuizAnswerRequest answerReq : request.getAnswers()) {
                QuizAnswer answer = QuizAnswer.builder()
                        .answerText(answerReq.getAnswerText())
                        .isCorrect(answerReq.getIsCorrect())
                        .order(answerReq.getOrder())
                        .question(saved)
                        .build();
                quizAnswerRepository.save(answer);
            }
            saved = questionRepository.findById(saved.getId()).get();
        }

        return mapToResponse(saved);
    }

    /**
     * Obtenir une question
     */
    public QuestionResponse getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée : " + questionId));
        return mapToResponse(question);
    }

    /**
     * Obtenir les questions d'un quiz
     */
    public List<QuestionResponse> getQuestionsByQuiz(Long quizId) {
        return questionRepository.findByQuizId(quizId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour une question
     */
    public QuestionResponse updateQuestion(Long questionId, CreateQuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée : " + questionId));

        if (request.getText() != null) question.setText(request.getText());
        if (request.getType() != null) question.setType(request.getType());
        if (request.getPoints() != null) question.setPoints(request.getPoints());
        if (request.getCorrectAnswer() != null) question.setCorrectAnswer(request.getCorrectAnswer());

        Question updated = questionRepository.save(question);
        return mapToResponse(updated);
    }

    /**
     * Supprimer une question
     */
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("Question non trouvée : " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    // ══ Mapper ══

    public QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType())
                .points(question.getPoints())
                .correctAnswer(question.getCorrectAnswer())
                .quizId(question.getQuiz().getId())
                .answers(question.getAnswers() != null 
                        ? question.getAnswers().stream()
                            .map(this::mapAnswerToResponse)
                            .collect(Collectors.toList())
                        : List.of())
                .build();
    }

    private QuizAnswerResponse mapAnswerToResponse(QuizAnswer answer) {
        return QuizAnswerResponse.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .isCorrect(answer.getIsCorrect())
                .order(answer.getOrder())
                .questionId(answer.getQuestion().getId())
                .build();
    }
}
