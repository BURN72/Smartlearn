package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.request.CreateModuleRequest;
import com.smartlearn.demo.dto.response.ModuleResponse;
import com.smartlearn.demo.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<ModuleResponse> createModule(
            @Valid @RequestBody CreateModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduleService.createModule(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ModuleResponse>> getModulesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public ResponseEntity<ModuleResponse> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody CreateModuleRequest request) {
        return ResponseEntity.ok(moduleService.updateModule(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}
