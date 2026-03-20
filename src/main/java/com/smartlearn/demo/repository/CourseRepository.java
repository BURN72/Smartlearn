package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByCategoryId(Long categoryId);

    List<Course> findByInstructorId(Long instructorId);

    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.category.id = :categoryId")
    List<Course> findByStatusAndCategory(@Param("status") CourseStatus status, @Param("categoryId") Long categoryId);

    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);
}
