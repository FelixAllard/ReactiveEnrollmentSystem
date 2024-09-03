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
    public static Enrollment toEnrollmentEntity(EnrollmentResponseModel enrollmentResponseModel) {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentResponseModel.getEnrollmentId());
        enrollment.setEnrollmentYear(enrollmentResponseModel.getEnrollmentYear());
        enrollment.setSemester(enrollmentResponseModel.getSemester());
        enrollment.setStudentId(enrollmentResponseModel.getStudentId());
        enrollment.setStudentFirstName(enrollmentResponseModel.getStudentFirstName());
        enrollment.setStudentLastName(enrollmentResponseModel.getStudentLastName());
        enrollment.setCourseId(enrollmentResponseModel.getCourseId());
        enrollment.setCourseName(enrollmentResponseModel.getCourseName());
        enrollment.setCourseNumber(enrollmentResponseModel.getCourseNumber());
        // Set other fields as needed
        return enrollment;
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
