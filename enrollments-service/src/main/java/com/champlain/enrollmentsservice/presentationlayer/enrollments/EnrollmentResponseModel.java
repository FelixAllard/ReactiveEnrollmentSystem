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
public class EnrollmentResponseModel {
    private String enrollmentId;
    private Integer enrollmentYear;
    private Semester semester;
    private String studentId;
    private String studentFirstName;
    private String studentLastName;
    private String courseId;
    private String courseNumber;
    private String courseName;
}
