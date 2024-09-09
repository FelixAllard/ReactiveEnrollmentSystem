package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceUnitTest {
    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentClientAsynchronous studentClient;
    @Mock
    private CourseClient courseClient;

    Enrollment enrollment1 = Enrollment.builder()
            .id(UUID.randomUUID().toString())
            .enrollmentId(UUID.randomUUID().toString())
            .enrollmentYear(2023)
            .semester(Semester.FALL)
            .studentId("studentExample")
            .courseId("courseExample")
            .build();

    Enrollment enrollment2 = Enrollment.builder()
            .id(UUID.randomUUID().toString())
            .enrollmentId(UUID.randomUUID().toString())
            .enrollmentYear(2023)
            .semester(Semester.FALL)
            .studentId("studentExample2")
            .courseId("courseExample")
            .build();

    //Code Along
    @Test
    public void whenGetEnrollmentById_thenReturnEnrollment() {

        StudentResponseModel studentResponse = new StudentResponseModel(
                enrollment1.getStudentId(),
                "John",
                "Pork",
                "Computer Science",
                "HumSigma"
        );

        CourseResponseModel courseResponse = new CourseResponseModel(
                enrollment1.getCourseId(),
                "CSJava101",
                "Java1",
                4,
                2.5,
                "CS"
        );

        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollment1.getEnrollmentId()))
                .thenReturn(Mono.just(enrollment1));
        when(studentClient.getStudentByStudentId(enrollment1.getStudentId()))
                .thenReturn(Mono.just(studentResponse)); // Mock the student response
        when(courseClient.getCourseByCourseId(enrollment1.getCourseId()))
                .thenReturn(Mono.just(courseResponse)); // Mock the course response


        Mono<EnrollmentResponseModel> enrollment = enrollmentService.getEnrollmentByEnrollmentId(enrollment1.getEnrollmentId());


        StepVerifier.create(enrollment)
                .expectNextMatches(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel.getEnrollmentId());
                    assertEquals(enrollmentResponseModel.getEnrollmentYear(), enrollment1.getEnrollmentYear());
                    assertEquals(enrollmentResponseModel.getSemester(), enrollment1.getSemester());
                    assertEquals(enrollmentResponseModel.getStudentId(), enrollment1.getStudentId());
                    assertEquals(enrollmentResponseModel.getStudentFirstName(), studentResponse.getFirstName());
                    assertEquals(enrollmentResponseModel.getStudentLastName(), studentResponse.getLastName());
                    assertEquals(enrollmentResponseModel.getCourseId(), enrollment1.getCourseId());
                    assertEquals(enrollmentResponseModel.getCourseNumber(), courseResponse.getCourseNumber());
                    assertEquals(enrollmentResponseModel.getCourseName(), courseResponse.getCourseName());
                    return true;
                })
                .verifyComplete();
    }

    //Me
    @Test
    public void whenDeleteEnrollmentById_thenDeleteEnrollmentAndReturnEnrollmentResponseModel() {

        Enrollment enrollment = Enrollment.builder()
                .id(UUID.randomUUID().toString())
                .enrollmentId(UUID.randomUUID().toString())
                .enrollmentYear(2023)
                .semester(Semester.FALL)
                .studentId("studentExample")
                .courseId("courseExample")
                .build();

        EnrollmentResponseModel expectedResponse = new EnrollmentResponseModel(
                enrollment.getEnrollmentId(),
                enrollment.getEnrollmentYear(),
                enrollment.getSemester(),
                enrollment.getStudentId(),
                enrollment.getStudentFirstName(),
                enrollment.getStudentLastName(),
                enrollment.getCourseId(),
                enrollment.getCourseNumber(),
                enrollment.getCourseName()
        );

        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollment.getEnrollmentId()))
                .thenReturn(Mono.just(enrollment));
        when(enrollmentRepository.delete(enrollment)).thenReturn(Mono.empty());

        Mono<EnrollmentResponseModel> result = enrollmentService.deleteEnrollmentByEnrollmentId(enrollment.getEnrollmentId());

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

}
