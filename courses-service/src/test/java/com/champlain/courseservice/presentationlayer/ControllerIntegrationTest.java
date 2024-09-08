package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.businesslayer.CourseServiceImpl;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.Math.log;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CourseRepository courseRepository;
    private final String validCourseId = "77918ba2-49da-4c67-bea8-9f26e5d355fb";
    private final Long dbSize = 1000L;

    @BeforeEach
    public void dbSetup() {
        // We just want to test that the db is at the original status every test.
        // Can be removed later
        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count -> {
                    assertEquals(dbSize, count);
                })
                .verifyComplete();
    }
    @Test
    void addNewCourse_shouldSucceed() {
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-423")
                .courseName("Web Services Testing")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertNotNull(courseResponseModel.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(),
                            courseResponseModel.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(),
                            courseResponseModel.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(),
                            courseResponseModel.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(),
                            courseResponseModel.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(),
                            courseResponseModel.getDepartment());
                });
    }
    @Test
    void getAllCourses_shouldReturnAllCourses() {
        webTestClient
                .get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertEquals(dbSize, list.size());
                });
    }
    @Test
    void getCourseByCourseId_shouldSucceedWithExistingId() {
        webTestClient
                .get()
                .uri("/api/v1/courses/" + validCourseId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .value(list -> {
                    assertNotNull(list);
                    assertEquals(1, list.size());
                    assertEquals(validCourseId, list.get(0).getCourseId());
                });
    }

    @Test
    void getCourseByCourseId_shouldReturnNotFound_WithNonExistingId() {
        webTestClient
                .get()
                .uri("/api/v1/courses/" + "77918ba2-49da-4c67-bea8-111111111111")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class);
    }
    @Test
    void getCourseByCourseId_shouldReturnUnProcessableEntity_WithInvalidId() {
        webTestClient
                .get()
                .uri("/api/v1/courses/" + "77918ba2-49da-4c67-bea8-111")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isEqualTo(422) // Unprocessable Entity
                .expectBody(String.class);
    }
    @Test
    void getAllCourses_whenNoCourses_shouldReturnEmptyStream() {
        courseRepository.deleteAll().block(); // Add block so the process won't be to fast
        webTestClient
                .get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertEquals(courseRepository.count().block(), list.size()); // Also block here cause else we get a problem
                    assertEquals(list.size(), 0);
                });

    }
    @Test
    void updateCourse_withValidCourseId_ShouldSucceed() {
        // Given
        CourseRequestModel courseRequestModel = CourseRequestModel.builder()
                .courseNumber("cat-423")
                .courseName("Web Services Testing")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();
        // When
        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", validCourseId)
                .bodyValue(courseRequestModel)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(CourseResponseModel.class)
                .value((updatedCourseResponse) -> {
                    assertNotNull(updatedCourseResponse);
                    assertEquals(validCourseId, updatedCourseResponse.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(),updatedCourseResponse.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(),updatedCourseResponse.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(),updatedCourseResponse.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(),updatedCourseResponse.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(),updatedCourseResponse.getDepartment());
                });
        webTestClient
                .get()
                .uri("/api/v1/courses/{courseId}", validCourseId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .returnResult(CourseResponseModel.class)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectNextMatches(updatedCourseResponse -> {
                    assertNotNull(updatedCourseResponse);
                    assertEquals(validCourseId, updatedCourseResponse.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(),updatedCourseResponse.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(),updatedCourseResponse.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(),updatedCourseResponse.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(),updatedCourseResponse.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(),updatedCourseResponse.getDepartment());
                    return true;
                })
                .verifyComplete();
    }
    @Test
    void updateCourse_withNonExistingCourseId_ShouldReturnNotFound(){

    }


}