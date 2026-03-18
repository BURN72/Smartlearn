package com.smartlearn.demo.dto.response;

import com.smartlearn.demo.entity.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private Long id;

    private String title;

    private LessonType type;

    private String content;

    private Integer duration;

    private Integer order;

    private Boolean isFree;

    private Long moduleId;
}
