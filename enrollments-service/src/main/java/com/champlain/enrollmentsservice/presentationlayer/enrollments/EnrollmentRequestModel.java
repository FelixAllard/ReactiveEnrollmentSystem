package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestModel {
    private Integer enrollmentYear;
    private Semester semester;
    private String studentId;
    private String courseId;
}
