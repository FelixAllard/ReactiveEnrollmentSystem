package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = CourseController.class)
class CourseControllerUnitTest {
    @Autowired
    private CourseController courseController;
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private CourseService courseService;

    @Test
    public void whenAddCourse_thenReturnCourseResponseModel() {
        // Arrange
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber(courseRequestModel.getCourseNumber())
                .courseName(courseRequestModel.getCourseName())
                .numHours(courseRequestModel.getNumHours())
                .numCredits(courseRequestModel.getNumCredits())
                .department(courseRequestModel.getDepartment())
                .build();
        Mockito.when(courseService.addCourse(any(Mono.class))).thenReturn(Mono.just(courseResponseModel));
        // Act
        webTestClient.post()
                .uri("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);
        verify(courseService, times(1)).addCourse(any(Mono.class));
    }
    @Test
    void getCourseByCourseId_validCourseId_courseReturned() {
        // Arrange
        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        Mockito.when(courseService.getCourseByCourseId(courseId)).thenReturn(Mono.just(courseResponseModel));

        webTestClient
                .get()
                .uri("/api/v1/courses/" + courseId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertEquals(courseResponseModel,list.get(0));
                });

        verify(courseService, times(1)).getCourseByCourseId(courseId);
    }
    @Test
    public void whenGetCourseByCourseId_withInvalidCourseId_thenReturnInvalidInputException(){
        String courseId = UUID.randomUUID().toString()+"3";

        String otherUUID = UUID.randomUUID().toString();
        while (otherUUID.equals(UUID.randomUUID().toString())) {
            otherUUID = UUID.randomUUID().toString();
        }

        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(otherUUID)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        Mockito.when(courseService.getCourseByCourseId(courseId)).thenReturn(Mono.just(courseResponseModel));

        webTestClient
                .get()
                .uri("/api/v1/courses/" + courseId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class);

        //verify(courseService, times(1)).getCourseByCourseId(courseId);
    }
    @Test
    public void whenUpdateCourse_thenReturnCourseResponseModel() {
        // Arrange
        String courseId = UUID.randomUUID().toString();
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber(courseRequestModel.getCourseNumber())
                .courseName(courseRequestModel.getCourseName())
                .numHours(courseRequestModel.getNumHours())
                .numCredits(courseRequestModel.getNumCredits())
                .department(courseRequestModel.getDepartment())
                .build();
        Mockito.when(courseService.updateCourse(any(Mono.class), eq(courseId))).thenReturn(Mono.just(courseResponseModel));

        // Act
        webTestClient.put()
                .uri("/api/v1/courses/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        verify(courseService, times(1)).updateCourse(any(Mono.class), eq(courseId));
    }
    @Test
    public void whenUpdateCourse_withInvalidCourseId_thenReturnInvalidInputException(){
        String courseId = UUID.randomUUID().toString();
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber(courseRequestModel.getCourseNumber())
                .courseName(courseRequestModel.getCourseName())
                .numHours(courseRequestModel.getNumHours())
                .numCredits(courseRequestModel.getNumCredits())
                .department(courseRequestModel.getDepartment())
                .build();
        Mockito.when(courseService.updateCourse(any(Mono.class), eq(courseId))).thenReturn(Mono.just(courseResponseModel));

        // Act
        webTestClient.put()
                .uri("/api/v1/courses/{courseId}", courseId+"1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class);
                //.isEqualTo(courseResponseModel);

        //verify(courseService, times(1)).updateCourse(any(Mono.class), eq(courseId));
    }
    @Test
    public void whenDeleteCourse_thenReturnCourseResponseModel() {
        // Arrange
        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        Mockito.when(courseService.deleteCourseByCourseId(eq(courseId))).thenReturn(Mono.just(courseResponseModel));

        // Act
        webTestClient.delete()
                .uri("/api/v1/courses/{courseId}", courseId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        //verify(courseService, times(1)).deleteCourse(eq(courseId));
    }
    @Test
    public void whenDeleteCourse_withInvalidCourseId_thenReturnInvalidInputException(){
        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        Mockito.when(courseService.deleteCourseByCourseId(eq(courseId))).thenReturn(Mono.just(courseResponseModel));

        // Act
        webTestClient.delete()
                .uri("/api/v1/courses/{courseId}", courseId+"1")//WE ADDING SOMETHING
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class);
                //.isEqualTo(courseResponseModel);
    }
}