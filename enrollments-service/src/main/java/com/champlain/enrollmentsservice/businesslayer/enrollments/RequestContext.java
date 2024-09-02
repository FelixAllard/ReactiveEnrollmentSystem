package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {
    private EnrollmentRequestModel enrollmentRequestModel;
    private EnrollmentResponseModel enrollmentResponseModel;
    private Enrollment enrollment;
    private CourseResponseModel courseResponseModel;

    public RequestContext(EnrollmentRequestModel enrollmentRequestModel, Enrollment enrollment) {
        this.enrollmentRequestModel = enrollmentRequestModel;
    }


}
