package com.smartlearn.demo.dto.response;

import com.smartlearn.demo.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private EnrollmentStatus status;

    private LocalDateTime enrolledAt;

    private Integer progress;

    private Long studentId;

    private String studentName;

    private Long courseId;

    private String courseName;

    private Boolean isPaid;
}
