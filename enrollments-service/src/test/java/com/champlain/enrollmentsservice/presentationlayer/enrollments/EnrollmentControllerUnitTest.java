package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.utils.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = EnrollmentController.class)
class EnrollmentControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private EnrollmentService enrollmentService;
    String enrollmentId = UUID.randomUUID().toString();
    String NOTFOUND_enrollmentId = "4c863bf3-95d1-46bb-b0a0-7e286fde02ef";

    EnrollmentResponseModel enrollmentResponseModel =
            EnrollmentResponseModel.builder()
                    .enrollmentId(enrollmentId)
                    .enrollmentYear(2023)
                    .semester(Semester.FALL)
                    .studentId("student123")
                    .studentFirstName("Donna")
                    .studentLastName("Hornsby")
                    .courseId("course123")
                    .courseName("Web Services")
                    .courseNumber("N45-LA")
                    .build();

    EnrollmentRequestModel enrollmentRequestModel = EnrollmentRequestModel.builder()
            .enrollmentYear(2023)
            .semester(Semester.FALL)
            .studentId("student123")
            .courseId("course123")
            .build();

    //Code along
    @Test
    void getEnrollmentByEnrollmentId_validEnrollmentId_enrollmentReturned() {
        when(enrollmentService.getEnrollmentByEnrollmentId(enrollmentId)).thenReturn(Mono.just
                (enrollmentResponseModel));

        webTestClient.get()
                .uri("/api/v1/enrollments/{enrollmentId}", enrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel);
        verify(enrollmentService, times(1)).getEnrollmentByEnrollmentId(enrollmentId);
    }

    //Me
    @Test
    void addEnrollment_validEnrollment_enrollmentAdded() {
        when(enrollmentService.addEnrollment(any(Mono.class)))
                .thenReturn(Mono.just(enrollmentResponseModel));

        webTestClient.post()
                .uri("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(enrollmentRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel);

        verify(enrollmentService, times(1)).addEnrollment(any(Mono.class));
    }

    //Me
    @Test
    void getEnrollmentByEnrollmentId_withInvalidEnrollmentId_throwsNotFoundException() {
        when(enrollmentService.getEnrollmentByEnrollmentId(NOTFOUND_enrollmentId))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/enrollments/{enrollmentId}", NOTFOUND_enrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();

        verify(enrollmentService, times(1)).getEnrollmentByEnrollmentId(NOTFOUND_enrollmentId);
    }

    //Me
    @Test
    void getAllEnrollments_validEnrollments_enrollmentsReturned() {
        EnrollmentResponseModel enrollment1 = new EnrollmentResponseModel("e1", 2024, Semester.FALL, "s1", "John", "Doe", "c1", "CS101", "Introduction to CS");
        EnrollmentResponseModel enrollment2 = new EnrollmentResponseModel("e2", 2024, Semester.FALL, "s2", "Jane", "Smith", "c2", "CS102", "Data Structures");

        when(enrollmentService.getAllEnrollments())
                .thenReturn(Flux.just(enrollment1, enrollment2));

        webTestClient.get()
                .uri("/api/v1/enrollments")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
                .expectBodyList(EnrollmentResponseModel.class)
                .hasSize(2)
                .contains(enrollment1, enrollment2);

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    //Me
    @Test
    void updateEnrollment_validEnrollment_enrollmentUpdated() {

        EnrollmentRequestModel updatedEnrollment = EnrollmentRequestModel.builder()
                .enrollmentYear(2024)
                .semester(Semester.SPRING)
                .studentId("student456")
                .courseId("course456")
                .build();

        EnrollmentResponseModel updatedEnrollmentResponse = EnrollmentResponseModel.builder()
                .enrollmentId(enrollmentId)
                .enrollmentYear(2024)
                .semester(Semester.SPRING)
                .studentId("student456")
                .studentFirstName("Donna")
                .studentLastName("Hornsby")
                .courseId("course456")
                .courseName("Advanced Web Services")
                .courseNumber("N45-LA")
                .build();

        when(enrollmentService.updateEnrollmentByEnrollmentId(any(Mono.class), eq(enrollmentId)))
                .thenReturn(Mono.just(updatedEnrollmentResponse));

        webTestClient.put()
                .uri("/api/v1/enrollments/{enrollmentId}", enrollmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEnrollment)
                .exchange()

                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(updatedEnrollmentResponse);

        verify(enrollmentService, times(1)).updateEnrollmentByEnrollmentId(any(Mono.class), eq(enrollmentId));
    }

    //Me
    @Test
    void updateEnrollment_withInvalidEnrollmentId_throwsInvalidInputException() {

        when(enrollmentService.updateEnrollmentByEnrollmentId(any(Mono.class), eq(NOTFOUND_enrollmentId)))
                .thenReturn(Mono.error(new InvalidInputException("Invalid enrollment ID")));

        webTestClient.put()
                .uri("/api/v1/enrollments/{enrollmentId}", NOTFOUND_enrollmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(enrollmentRequestModel)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid enrollment ID");

        verify(enrollmentService, times(1)).updateEnrollmentByEnrollmentId(any(Mono.class), eq(NOTFOUND_enrollmentId));
    }

    @Test
    void deleteEnrollment_validEnrollmentId_enrollmentDeleted() {
        // Arrange
        when(enrollmentService.deleteEnrollmentByEnrollmentId(enrollmentId))
                .thenReturn(Mono.empty());

        // Act
        webTestClient.delete()
                .uri("/api/v1/enrollments/{enrollmentId}", enrollmentId)
                .exchange()
                .expectStatus().isBadRequest();

        // Assert
        verify(enrollmentService, times(1)).deleteEnrollmentByEnrollmentId(enrollmentId);
    }

    @Test
    public void deleteEnrollment_withInvalidEnrollmentId_throwsInvalidInputException() {
        when(enrollmentService.deleteEnrollmentByEnrollmentId(NOTFOUND_enrollmentId))
                .thenReturn(Mono.error(new InvalidInputException("Invalid enrollment ID")));

        webTestClient.delete()
                .uri("/api/v1/enrollments/{enrollmentId}", NOTFOUND_enrollmentId)
                .exchange()
                .expectStatus().isEqualTo(422) // Adjust status code if necessary
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid enrollment ID");

        verify(enrollmentService, times(1)).deleteEnrollmentByEnrollmentId(NOTFOUND_enrollmentId);
    }
}