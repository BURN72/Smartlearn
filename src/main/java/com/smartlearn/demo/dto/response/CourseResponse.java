package com.smartlearn.demo.dto.response;

import com.smartlearn.demo.entity.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private CourseStatus status;

    private String level;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long categoryId;

    private String categoryName;

    private Long instructorId;

    private String instructorName;
}
