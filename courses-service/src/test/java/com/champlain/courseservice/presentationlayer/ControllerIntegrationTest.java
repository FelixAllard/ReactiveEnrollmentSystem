package com.champlain.courseservice.presentationlayer;


import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CourseRepository courseRepository;
    private String validCourseId;
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


}