package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;


@RestController
@Slf4j
@RequestMapping("api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping(value="", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CourseResponseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping(value="/{courseId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> getCourseByCourseId(@PathVariable("courseId") String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36) //validate the courseId
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided courseId is invalid " + courseId)))
                .flatMap(courseService::getCourseByCourseId)
                .map(ResponseEntity::ok);
    }

    /**
     * Controller for AddCourse. Will use defaultIfEmpty in case something goes wrong!
     * @param courseRequestModel
     * @return
     */
    @PostMapping(value="", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> addCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel){
        return courseService.addCourse(courseRequestModel)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * This is for the updateCourseByCourseId function
     * @param courseRequestModel
     * @param courseId
     * @return
     */
    @PutMapping(value="/{courseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> updateCourseByCourseId(@RequestBody Mono<CourseRequestModel> courseRequestModel, @PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36) //validate the courseId
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided courseId is invalid " + courseId)))
                .flatMap(id -> courseService.updateCourse(courseRequestModel, id))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> deleteCourseByCourseId(@PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36) //validate the course id
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Course id is invalid: " + courseId)))
                .flatMap(courseService::deleteCourseByCourseId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
