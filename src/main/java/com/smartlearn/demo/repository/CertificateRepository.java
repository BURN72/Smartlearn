package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStudentId(Long studentId);

    Optional<Certificate> findByUniqueCode(String uniqueCode);

    List<Certificate> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Certificate> findByCourseId(Long courseId);
}
