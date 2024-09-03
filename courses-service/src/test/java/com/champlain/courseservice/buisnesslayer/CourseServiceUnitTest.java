package com.champlain.courseservice.buisnesslayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.businesslayer.CourseServiceImpl;
import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceUnitTest {
    @InjectMocks
    private CourseServiceImpl courseService;
    @Mock
    private CourseRepository courseRepository;
    Course course1 = Course.builder()
            .id(1)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-420")
            .courseName("Web Services")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();
    Course course2 = Course.builder()
            .id(2)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-421")
            .courseName("Advanced Web Services")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();
    Course course3 = Course.builder()
            .id(3)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-422")
            .courseName("Web Services Security")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    @Test
    void addCourse_shouldReturnCourseResponseModel() {
        Mockito.when(courseRepository.save(any(Course.class)))
                .thenReturn(Mono.just(course1));
        CourseRequestModel courseRequestModel = new CourseRequestModel();
        BeanUtils.copyProperties(course1, courseRequestModel);
        Mono<CourseResponseModel> result =
                courseService.addCourse(Mono.just(courseRequestModel));
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    System.out.println(courseResponseModel);
                    assertNotNull(courseResponseModel.getCourseId());
                    assertEquals(courseResponseModel.getCourseNumber(),
                            courseRequestModel.getCourseNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void whenGetAllCourses_thenReturnAllCourses() {
        //make sure this is a Mockito when
        Mockito.when(courseRepository.findAll())
                .thenReturn(Flux.just(course1, course2, course3));
        //act
        Flux<CourseResponseModel> result = courseService.getAllCourses();
        //assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel.getCourseId());
                    assertEquals(courseResponseModel.getCourseNumber(),
                            course1.getCourseNumber());
                    return true;
                })
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getCourseNumber().equals("cat-421"))
                .expectNextMatches(courseResponseModel ->
                        courseResponseModel.getCourseNumber().equals("cat-422"))
                .verifyComplete();
    }
}

