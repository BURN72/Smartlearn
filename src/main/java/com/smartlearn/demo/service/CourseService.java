package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateCourseRequest;
import com.smartlearn.demo.dto.request.UpdateCourseRequest;
import com.smartlearn.demo.dto.response.CourseDetailResponse;
import com.smartlearn.demo.dto.response.CourseResponse;
import com.smartlearn.demo.entity.Category;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Module;
import com.smartlearn.demo.entity.User;
import com.smartlearn.demo.entity.enums.CourseStatus;
import com.smartlearn.demo.repository.CategoryRepository;
import com.smartlearn.demo.repository.CourseRepository;
import com.smartlearn.demo.repository.LessonRepository;
import com.smartlearn.demo.repository.ModuleRepository;
import com.smartlearn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final ModuleService moduleService;

    public CourseResponse createCourse(CreateCourseRequest request) {
        // Get current instructor
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .level(request.getLevel())
                .status(CourseStatus.BROUILLON)
                .category(category)
                .instructor(instructor)
                .build();

        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }

    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            course.setCategory(category);
        }
        if (request.getStatus() != null) {
            try {
                course.setStatus(CourseStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Statut invalide : " + request.getStatus());
            }
        }
        course.setUpdatedAt(LocalDateTime.now());

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));

        List<Module> modules = moduleRepository.findByCourseId(courseId);
        int totalLessons = modules.stream()
                .mapToInt(m -> lessonRepository.findByModuleId(m.getId()).size())
                .sum();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getStatus())
                .level(course.getLevel())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getName())
                .modules(modules.stream()
                        .map(moduleService::mapToResponse)
                        .collect(Collectors.toList()))
                .enrollmentCount(course.getEnrollments() != null ? course.getEnrollments().size() : 0)
                .totalLessons(totalLessons)
                .build();
    }

    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        return mapToResponse(course);
    }

    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatusOrderByCreatedAtDesc(CourseStatus.PUBLIE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByStatus(String status) {
        try {
            CourseStatus courseStatus = CourseStatus.valueOf(status);
            return courseRepository.findByStatus(courseStatus)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide : " + status);
        }
    }

    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Cours non trouvé : " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        course.setStatus(CourseStatus.PUBLIE);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    public void submitForReview(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        course.setStatus(CourseStatus.EN_REVISION);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    public void rejectCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        course.setStatus(CourseStatus.REJETE);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    public void archiveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + courseId));
        course.setStatus(CourseStatus.ARCHIVE);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    // ══ Mappers ══

    public CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getStatus())
                .level(course.getLevel())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getName())
                .build();
    }
   
    public List<CourseResponse> getCoursesByInstructorEmail(String email) {
        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return courseRepository.findByInstructorId(instructor.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
