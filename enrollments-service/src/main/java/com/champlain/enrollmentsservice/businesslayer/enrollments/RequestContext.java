package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {
    private EnrollmentRequestModel enrollmentRequestModel;
    private Enrollment enrollment;
    private StudentResponseModel studentResponseModel;
    private CourseResponseModel courseResponseModel;
    public RequestContext(EnrollmentRequestModel enrollmentRequestModel) {
        this.enrollmentRequestModel = enrollmentRequestModel;
    }
}

