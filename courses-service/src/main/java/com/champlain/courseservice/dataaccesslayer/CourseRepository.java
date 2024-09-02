package com.champlain.courseservice.dataaccesslayer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CourseRepository extends ReactiveCrudRepository<Course, Integer> {

    Mono<Course> findCourseByCourseId(String courseId);
}
