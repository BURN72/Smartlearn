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
public class CertificateResponse {

    private Long id;

    private String uniqueCode;

    private LocalDateTime issuedAt;

    private String certificateUrl;

    private Long studentId;

    private String studentName;

    private Long courseId;

    private String courseName;

    private String instructorName;
}
