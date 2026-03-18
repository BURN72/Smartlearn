package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.request.CreateEnrollmentRequest;
import com.smartlearn.demo.dto.response.EnrollmentResponse;
import com.smartlearn.demo.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @Valid @RequestBody CreateEnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.createEnrollment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> activateEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.activateEnrollment(id));
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponse> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        return ResponseEntity.ok(enrollmentService.updateProgress(id, progress));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<EnrollmentResponse> cancelEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.cancelEnrollment(id));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EnrollmentResponse> refundEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.refundEnrollment(id));
    }
}
