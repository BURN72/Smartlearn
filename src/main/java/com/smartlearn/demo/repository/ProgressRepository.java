package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    List<Progress> findByStudentId(Long studentId);

    List<Progress> findByLessonId(Long lessonId);

    List<Progress> findByLessonIdAndCompletedAtIsNotNull(Long lessonId);

    List<Progress> findByStudentIdAndLessonModuleCourseIdAndCompletedAtIsNotNull(Long studentId, Long courseId);

    List<Progress> findByStudentIdAndLessonModuleCourseId(Long studentId, Long courseId);
}
