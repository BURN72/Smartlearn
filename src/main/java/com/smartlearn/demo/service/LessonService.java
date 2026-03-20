package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.request.CreateLessonRequest;
import com.smartlearn.demo.dto.response.LessonResponse;
import com.smartlearn.demo.entity.Lesson;
import com.smartlearn.demo.entity.Module;
import com.smartlearn.demo.entity.enums.LessonType;
import com.smartlearn.demo.repository.LessonRepository;
import com.smartlearn.demo.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonResponse createLesson(CreateLessonRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module non trouvé : " + request.getModuleId()));

        try {
            LessonType lessonType = LessonType.valueOf(request.getType());

            Lesson lesson = Lesson.builder()
                    .title(request.getTitle())
                    .type(lessonType)
                    .content(request.getContent())
                    .duration(request.getDuration())
                    .orderIndex(request.getOrderIndex())
                    .isFree(request.getIsFree() != null ? request.getIsFree() : true)
                    .module(module)
                    .build();

            Lesson saved = lessonRepository.save(lesson);
            return mapToResponse(saved);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Type de leçon invalide : " + request.getType());
        }
    }

    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée : " + lessonId));
        return mapToResponse(lesson);
    }

    public List<LessonResponse> getLessonsByModule(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId)
                .stream()
                .sorted((l1, l2) -> Integer.compare(l1.getOrderIndex(), l2.getOrderIndex()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LessonResponse updateLesson(Long lessonId, CreateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée : " + lessonId));

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getContent() != null) lesson.setContent(request.getContent());
        if (request.getDuration() != null) lesson.setDuration(request.getDuration());
        if (request.getOrderIndex() != null) lesson.setOrderIndex(request.getOrderIndex());
        if (request.getIsFree() != null) lesson.setIsFree(request.getIsFree());

        Lesson updated = lessonRepository.save(lesson);
        return mapToResponse(updated);
    }

    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new RuntimeException("Leçon non trouvée : " + lessonId);
        }
        lessonRepository.deleteById(lessonId);
    }

    // ══ Mapper ══

    public LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .content(lesson.getContent())
                .duration(lesson.getDuration())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .moduleId(lesson.getModule().getId())
                .build();
    }
}
