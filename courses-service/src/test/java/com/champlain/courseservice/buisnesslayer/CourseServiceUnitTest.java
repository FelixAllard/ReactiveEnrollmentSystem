package com.champlain.courseservice.buisnesslayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.businesslayer.CourseServiceImpl;
import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import com.champlain.courseservice.utils.exceptions.InvalidInputException;
import com.champlain.courseservice.utils.exceptions.NotFoundException;
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
    void getCourseByCourseId_withNonExistingId_thenThrowNotFoundException(){
        // Given : WRONG ID
        String courseId = "77918ba2-49da-4c67-bea8-111111111111";
        Mockito.when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.empty());
        // When
        Mono<CourseResponseModel> result = courseService.getCourseByCourseId(courseId);

        // Then
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }


    @Test
    void getCourseByCourseId_withExistingId_thenReturnCourseResponseModel(){
        // Given
        String courseId = course1.getCourseId();
        Mockito.when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.just(course1));

        // When
        Mono<CourseResponseModel> result = courseService.getCourseByCourseId(courseId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(courseId, courseResponseModel.getCourseId());
                    assertEquals(course1.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(course1.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(course1.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(course1.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(course1.getDepartment(), courseResponseModel.getDepartment());
                    return true;
                })
                .verifyComplete();
    }
    @Test
    void updateCourseByCourseId_withExistingCourseId_thenReturnUpdatedCourseResponseModel(){
        // Given
        String courseId = course1.getCourseId();
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-420-updated")
                .courseName("Web Services Updated")
                .numHours(50)
                .numCredits(4.0)
                .department("Computer Science Updated")
                .build();

        Mockito.when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.just(course1));
        course1.setCourseNumber(courseRequestModel.getCourseNumber()); // Update course number
        course1.setCourseName(courseRequestModel.getCourseName()); // Update course name
        course1.setNumHours(courseRequestModel.getNumHours());
        course1.setNumCredits(courseRequestModel.getNumCredits());
        course1.setDepartment(courseRequestModel.getDepartment());
        Mockito.when(courseRepository.save(any(Course.class))).thenReturn(Mono.just(course1));

        // When
        Mono<CourseResponseModel> result = courseService.updateCourseByCourseId(Mono.just(courseRequestModel), courseId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(courseId, courseResponseModel.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), courseResponseModel.getDepartment());
                    return true;
                })
                .verifyComplete();
    }
    @Test
    void updateCourseByCourseId_withNonExistingCourseId_thenThrowNotFoundException(){
        // Given
        String courseId = course1.getCourseId();
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-420-updated")
                .courseName("Web Services Updated")
                .numHours(50)
                .numCredits(4.0)
                .department("Computer Science Updated")
                .build();

        Mockito.when(courseRepository.findCourseByCourseId(courseId)).thenReturn(Mono.just(course1));
        course1.setCourseNumber(courseRequestModel.getCourseNumber()); // Update course number
        course1.setCourseName(courseRequestModel.getCourseName()); // Update course name
        course1.setNumHours(courseRequestModel.getNumHours());
        course1.setNumCredits(courseRequestModel.getNumCredits());
        course1.setDepartment(courseRequestModel.getDepartment());
        Mockito.when(courseRepository.save(any(Course.class))).thenReturn(Mono.just(course1));

        // When
        Mono<CourseResponseModel> result = courseService.updateCourseByCourseId(Mono.just(courseRequestModel), courseId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertEquals(courseId, courseResponseModel.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), courseResponseModel.getDepartment());
                    return true;
                })
                .expectError(NotFoundException.class)
                .verify();
    }
    @Test
    void deleteCourseByCourseId_withExistingCourseId_ReturnsDeletedCourseId(){}
    @Test
    void deleteCourseByCourseId_withNonExistingCourseId_thenThrowNotFoundException(){}


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

