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
                .doOnNext(course -> log.debug("Found course: " + course))
                .map(EntityModelUtil::toCourseResponseModel)
                .doOnNext(response -> log.debug("Mapped response: " + response))
                .log();
    }


    /**
     * Controller method. Creates a course into the database
     * @param courseRequestModel
     * @return
     */
    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelUtil::toCourseEntity) // This transform the CourseRequestModel into a Course
                .doOnNext(e -> e.setCourseId(EntityModelUtil.generateUUIDString())) // We generate a set of course ID
                .flatMap(courseRepository::save) // We Insert into the database
                .map(EntityModelUtil::toCourseResponseModel);
    }

    /**
     * We bring in the courseRequestModel and the string course id
     * @param courseRequestModel
     * @param courseId
     * @return
     */
    @Override
    public Mono<CourseResponseModel> updateCourseByCourseId(Mono<CourseRequestModel> courseRequestModel, String
            courseId) {
        return courseRepository.findCourseByCourseId(courseId)
        //if the course exists, the repo will emit an entity and the flow

                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId)))) // Throw an error if empty
                //get the courseRequestModel that was provided
                .flatMap(s -> courseRequestModel
                        //convert to a NEW Course entity
                        .map(EntityModelUtil::toCourseEntity)
                        //get the CourseId from the entity we got from the db
                        .doOnNext(e -> e.setCourseId(s.getCourseId()))
                        //get the id from the entity we got from the db
                        .doOnNext(e -> e.setId(s.getId())) )
                .flatMap(courseRepository::save) //save the new Course Entity
                //convert the new Entity (returned by the db) to a CourseResponseModel
                .map(EntityModelUtil::toCourseResponseModel);
    }
    //TODO check if it is needed

    /**
     * We accept a courseId and give back a courseResponseModel
     * @param courseId
     * @return CourseResponseModel
     */
    @Override // ADDED THIS OVERIDE
    public Mono<CourseResponseModel> deleteCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId))))
                .flatMap(existingCourse -> courseRepository.delete(existingCourse).then(Mono.just(existingCourse)))
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
