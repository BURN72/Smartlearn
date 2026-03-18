package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateModuleRequest;
import com.smartlearn.demo.dto.response.ModuleResponse;
import com.smartlearn.demo.entity.Course;
import com.smartlearn.demo.entity.Lesson;
import com.smartlearn.demo.entity.Module;
import com.smartlearn.demo.repository.CourseRepository;
import com.smartlearn.demo.repository.LessonRepository;
import com.smartlearn.demo.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonService lessonService;

    public ModuleResponse createModule(CreateModuleRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé : " + request.getCourseId()));

        Module module = Module.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .order(request.getOrder())
                .course(course)
                .build();

        Module saved = moduleRepository.save(module);
        return mapToResponse(saved);
    }

    public ModuleResponse getModuleById(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module non trouvé : " + moduleId));
        return mapToResponse(module);
    }

    public List<ModuleResponse> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseId(courseId)
                .stream()
                .sorted((m1, m2) -> Integer.compare(m1.getOrder(), m2.getOrder()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ModuleResponse updateModule(Long moduleId, CreateModuleRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module non trouvé : " + moduleId));

        if (request.getTitle() != null) module.setTitle(request.getTitle());
        if (request.getDescription() != null) module.setDescription(request.getDescription());
        if (request.getOrder() != null) module.setOrder(request.getOrder());

        Module updated = moduleRepository.save(module);
        return mapToResponse(updated);
    }

    public void deleteModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new RuntimeException("Module non trouvé : " + moduleId);
        }
        moduleRepository.deleteById(moduleId);
    }

    // ══ Mapper ══

    public ModuleResponse mapToResponse(Module module) {
        List<Lesson> lessons = lessonRepository.findByModuleId(module.getId());
        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .order(module.getOrder())
                .courseId(module.getCourse().getId())
                .lessons(lessons.stream()
                        .map(lessonService::mapToResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
