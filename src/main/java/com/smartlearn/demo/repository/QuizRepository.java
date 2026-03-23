package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByModuleId(Long moduleId);

    List<Quiz> findByModuleCourseId(Long courseId);
}
