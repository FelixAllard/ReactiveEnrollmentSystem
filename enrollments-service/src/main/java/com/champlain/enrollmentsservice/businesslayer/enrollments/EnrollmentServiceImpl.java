package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.utils.EntityModelUtil;
import com.champlain.enrollmentsservice.utils.exceptions.EnrollmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    final private StudentClientAsynchronous studentClient;
    final private CourseClient courseClient;
    final private EnrollmentRepository enrollmentRepository;
    Logger logger = Logger.getLogger(EnrollmentServiceImpl.class.getName());


    public EnrollmentServiceImpl(StudentClientAsynchronous studentClient,
                                 CourseClient courseClient,
                                 EnrollmentRepository enrollmentRepository) {
        this.studentClient = studentClient;
        this.courseClient = courseClient;
        this.enrollmentRepository = enrollmentRepository;
    }


    @Override
    public Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel>
                                                               enrollmentRequestModel) {
        return enrollmentRequestModel
                .map(RequestContext::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse)
                .map(EntityModelUtil::toEnrollmentEntity)
                .map(enrollmentRepository::save)
                .flatMap(entity -> entity)
                .map(EntityModelUtil::toEnrollmentResponseModel);
    }


    public Mono<CourseResponseModel> courseRequestResponse(String courseId) {
        return courseClient.getCourseByCourseId(courseId);
    }
    private Mono<RequestContext> studentRequestResponse(RequestContext rc) {
        return this.studentClient
                .getStudentByStudentId(rc.getEnrollmentRequestModel().getStudentId())
                .doOnNext(rc::setStudentResponseModel)
                .thenReturn(rc);
    }

    private Mono<RequestContext> courseRequestResponse(RequestContext rc) {
        return this.courseClient
                .getCourseByCourseId(rc.getEnrollmentRequestModel().getCourseId())
                .doOnNext(rc::setCourseResponseModel)
                .thenReturn(rc)
                .onErrorResume(e -> {
                    logger.info("Error processing course request: {}" +  e.getMessage() + e);
                    return Mono.error(new EnrollmentException("Failed to process Course", e));
                });
    }


}

