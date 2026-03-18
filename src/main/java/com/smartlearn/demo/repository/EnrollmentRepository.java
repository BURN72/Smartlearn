package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
