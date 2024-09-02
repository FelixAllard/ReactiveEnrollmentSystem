package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import com.champlain.courseservice.utils.EntityModelUtil;
import com.champlain.courseservice.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serial;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCourses() {
        return courseRepository.findAll()
                .map(EntityModelUtil::toCourseResponseModel);
    }

    @Override
    @GetMapping
    public Mono<CourseResponseModel> getCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course id not found " + courseId)))
                .doOnNext(i -> log.debug("The course entity is: " + i.toString())).map(EntityModelUtil::toCourseResponseModel)
                .log();
    }

    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelUtil::toCourseEntity)
                .doOnNext(e -> e.setCourseId(EntityModelUtil.generateUUIDString()))
                .flatMap(courseRepository::save)
                .map(EntityModelUtil::toCourseResponseModel);
    }

    @Override
    public Mono<CourseResponseModel> updateCourse(Mono<CourseRequestModel> courseRequestModel, String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId))))
                .flatMap(s ->courseRequestModel
                .map(EntityModelUtil::toCourseEntity)
                .doOnNext(e -> e.setCourseId(s.getCourseId()))
                        .doOnNext(e -> e.setId(s.getId()))
                )
                .flatMap(courseRepository::save)
                .map(EntityModelUtil::toCourseResponseModel);
    }
}
