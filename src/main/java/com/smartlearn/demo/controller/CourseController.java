package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.request.CreateCourseRequest;
import com.smartlearn.demo.dto.request.UpdateCourseRequest;
import com.smartlearn.demo.dto.response.CourseDetailResponse;
import com.smartlearn.demo.dto.response.CourseResponse;
import com.smartlearn.demo.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseDetail(id));
    }

    @GetMapping("/{id}/basic")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/published/all")
    public ResponseEntity<List<CourseResponse>> getPublishedCourses() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CourseResponse>> getCoursesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(courseService.getCoursesByStatus(status));
    }

    @GetMapping("/instructor/my-courses")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<CourseResponse>> getMyInstructorCourses() {
        // TODO: Get instructor ID from security context
        return ResponseEntity.ok(List.of());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{id}/submit-review")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<CourseResponse> submitForReview(@PathVariable Long id) {
        courseService.submitForReview(id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> rejectCourse(@PathVariable Long id) {
        courseService.rejectCourse(id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseResponse> archiveCourse(@PathVariable Long id) {
        courseService.archiveCourse(id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
}
