package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.QuizAttempt;
import com.smartlearn.demo.entity.enums.QuizAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByStudentId(Long studentId);

    List<QuizAttempt> findByQuizId(Long quizId);

    List<QuizAttempt> findByStudentIdAndQuizId(Long studentId, Long quizId);

    List<QuizAttempt> findByStatus(QuizAttemptStatus status);
}
