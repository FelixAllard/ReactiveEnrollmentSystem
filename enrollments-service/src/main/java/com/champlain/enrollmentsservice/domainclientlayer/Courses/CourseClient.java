package com.champlain.enrollmentsservice.domainclientlayer.Courses;

import com.champlain.enrollmentsservice.utils.HttpErrorInfo;
import com.champlain.enrollmentsservice.utils.exceptions.InvalidInputException;
import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;

@Service
public class CourseClient {

    private final WebClient webClient;
    private final String courseClientServiceBaseURL;

    public CourseClient(@Value("${app.courses-service.host}") String coursesServiceHost,
                         @Value("${app.courses-service.port}") String coursesServicePort) {
        courseClientServiceBaseURL = "http://" + coursesServiceHost + ":" + coursesServicePort + "/api/v1/courses";

        this.webClient = WebClient.builder()
                .baseUrl(courseClientServiceBaseURL)
                .build();
    }

    public Mono<CourseResponseModel> getCourseByCourseId(final String courseId) {
        return webClient.get()
                .uri("/{courseId}", courseId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        error -> switch(error.statusCode().value())
                        {
                            case 404 ->
                                    Mono.error(new
                                            NotFoundException("CourseId not found: " + courseId));
                            case 422 ->
                                    Mono.error(new
                                            InvalidInputException("CourseId invalid: " + courseId));
                            default ->
                                    Mono.error(new
                                            IllegalArgumentException("Something went wrong"));
                        }
                )
                .bodyToMono(CourseResponseModel.class);
    }

}
