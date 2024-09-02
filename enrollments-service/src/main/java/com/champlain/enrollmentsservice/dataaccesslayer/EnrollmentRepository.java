package com.champlain.enrollmentsservice.dataaccesslayer;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface EnrollmentRepository extends ReactiveMongoRepository<Enrollment, String>{

    Mono<Enrollment> findEnrollmentByEnrollmentId(String enrollmentId);
}
