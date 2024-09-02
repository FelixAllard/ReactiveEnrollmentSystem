package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    final private StudentClientAsynchronous studentClient;
    final private CourseClient courseClient;

    public EnrollmentServiceImpl(StudentClientAsynchronous studentClient, CourseClient courseClient) {
        this.studentClient = studentClient;
        this.courseClient = courseClient;
    }

    @Override
    public Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .map(RequestContext::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse);
    }

    private Mono<RequestContext> studentRequestResponse(RequestContext rc) {
        return this.studentClient
                .getStudentByStudentId(rc.getEnrollmentRequestModel().getStudentId())
                .doOnNext(rc::setStudentResponseModel)
                .thenReturn(rc);
    }

    private Mono<RequestContext> courseRequestResponse(RequestContext rc) {
        return this.courseClient.getCourseByCourseId(rc.getEnrollmentRequestModel().getCourseId())
                        .doOnNext(rc::setCourseResponseModel)
                        .thenReturn(rc);
    }
}

