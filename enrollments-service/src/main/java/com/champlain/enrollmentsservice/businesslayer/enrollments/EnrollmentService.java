package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import reactor.core.publisher.Mono;

public interface EnrollmentService {

    Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel);
}
