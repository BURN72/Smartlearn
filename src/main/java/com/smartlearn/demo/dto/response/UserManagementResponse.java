package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private Long coursesCreated;
    private Long studentsEnrolled;
    private Long coursesEnrolled;
}
