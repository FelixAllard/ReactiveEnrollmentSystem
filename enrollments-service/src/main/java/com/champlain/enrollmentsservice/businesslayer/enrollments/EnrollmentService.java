package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnrollmentService {

    Flux<EnrollmentResponseModel> getAllEnrollments();
    Mono<EnrollmentResponseModel> getEnrollmentByEnrollmentId(String enrollmentId);
    Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel);
    Mono<EnrollmentResponseModel> updateEnrollmentByEnrollmentId(Mono<EnrollmentRequestModel> enrollmentRequestModelMono, String enrollmentId);
    Mono<EnrollmentResponseModel> deleteEnrollmentByEnrollmentId(String enrollmentId);
}
