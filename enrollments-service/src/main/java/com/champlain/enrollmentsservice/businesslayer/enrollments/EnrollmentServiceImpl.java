package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.utils.EntityModelUtil;
import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    final private StudentClientAsynchronous studentClient;
    final private CourseClient courseClient;
    final private EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(StudentClientAsynchronous studentClient, CourseClient courseClient, EnrollmentRepository enrollmentRepository) {
        this.studentClient = studentClient;
        this.courseClient = courseClient;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Flux<EnrollmentResponseModel> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .flatMap(enrollment -> Mono.zip(
                        studentClient.getStudentByStudentId(enrollment.getStudentId()),
                        courseClient.getCourseByCourseId(enrollment.getCourseId()),
                        Mono.just(enrollment)
                ))
                .map(tuple -> {
                    var enrollment = tuple.getT3();
                    var student = tuple.getT1();
                    var course = tuple.getT2();

                    enrollment.setStudentFirstName(student.getFirstName());
                    enrollment.setStudentLastName(student.getLastName());
                    enrollment.setCourseNumber(course.getCourseNumber());
                    enrollment.setCourseName(course.getCourseName());

                    return EntityModelUtil.toEnrollmentResponseModel(enrollment);
                });
    }

    @Override
    public Mono<EnrollmentResponseModel> getEnrollmentByEnrollmentId(String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.error(new NotFoundException("Enrollment id not found " + enrollmentId)))
                .flatMap(enrollment -> Mono.zip(
                        studentClient.getStudentByStudentId(enrollment.getStudentId()),
                        courseClient.getCourseByCourseId(enrollment.getCourseId()),
                        Mono.just(enrollment)
                ))
                .map(tuple -> {
                    var enrollment = tuple.getT3();
                    var student = tuple.getT1();
                    var course = tuple.getT2();

                    enrollment.setStudentFirstName(student.getFirstName());
                    enrollment.setStudentLastName(student.getLastName());
                    enrollment.setCourseNumber(course.getCourseNumber());
                    enrollment.setCourseName(course.getCourseName());

                    return EntityModelUtil.toEnrollmentResponseModel(enrollment);
                });
    }

    @Override
    public Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .map(RequestContext::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse)
                .map(EntityModelUtil::toEnrollmentEntity)
                .map(enrollmentRepository::save)
                .flatMap(entity -> entity)
                .map(EntityModelUtil::toEnrollmentResponseModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> updateEnrollmentByEnrollmentId(Mono<EnrollmentRequestModel> enrollmentRequestModel, String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Enrollment id not found " + enrollmentId))))
                .flatMap(s -> enrollmentRequestModel
                        .map(RequestContext::new)
                        .flatMap(this::studentRequestResponse)
                        .flatMap(this::courseRequestResponse)
                        .map(EntityModelUtil::toEnrollmentEntity)
                        .doOnNext(e -> e.setEnrollmentId(s.getEnrollmentId()))
                        .doOnNext(e -> e.setId(s.getId())))
                .flatMap(enrollmentRepository::save)
                .map(EntityModelUtil::toEnrollmentResponseModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> deleteEnrollmentByEnrollmentId(String enrollmentId) {
        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Enrollment id not found " + enrollmentId))))
                .flatMap(enrollment -> {
                    EnrollmentResponseModel responseModel = convertToResponseModel(enrollment);

                    return enrollmentRepository.delete(enrollment)
                            .then(Mono.just(responseModel));
                });
    }

    private Mono<RequestContext> studentRequestResponse(RequestContext rc) {
        return this.studentClient
                .getStudentByStudentId(rc.getEnrollmentRequestModel().getStudentId())
                .doOnNext(rc::setStudentResponseModel)
                .thenReturn(rc);
    }

    private Mono<RequestContext> courseRequestResponse(RequestContext rc) {
        return this.courseClient
                .getCourseByCourseId(rc.getEnrollmentRequestModel().getCourseId())
                .doOnNext(rc::setCourseResponseModel)
                .thenReturn(rc);
    }

    private EnrollmentResponseModel convertToResponseModel(Enrollment enrollment) {
        return new EnrollmentResponseModel(
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
    }
}
