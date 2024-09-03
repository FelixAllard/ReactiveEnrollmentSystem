package com.champlain.enrollmentsservice.utils;

import com.champlain.enrollmentsservice.businesslayer.enrollments.RequestContext;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class EntityModelUtil {
    public static Enrollment toEnrollmentEntity(RequestContext rc) {
        return Enrollment.builder()
                .enrollmentId(generateUUIDString())
                .enrollmentYear(rc.getEnrollmentRequestModel().getEnrollmentYear())
                .semester(rc.getEnrollmentRequestModel().getSemester())
                .studentId(rc.getStudentResponseModel().getStudentId())
                .studentFirstName(rc.getStudentResponseModel().getFirstName())
                .studentLastName(rc.getStudentResponseModel().getLastName())
                .courseId(rc.getCourseResponseModel().getCourseId())
                .courseName(rc.getCourseResponseModel().getCourseName())
                .courseNumber(rc.getCourseResponseModel().getCourseNumber())
                .build();
    }
    public static EnrollmentResponseModel toEnrollmentResponseModel(Enrollment
                                                                            enrollment) {
        EnrollmentResponseModel enrollmentResponseModel = new
                EnrollmentResponseModel();
        BeanUtils.copyProperties(enrollment, enrollmentResponseModel);
        return enrollmentResponseModel;
    }
    public static String generateUUIDString() {
        return UUID.randomUUID().toString();
    }
}
