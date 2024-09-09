package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class MockServerConfigCoursesService {
    private static final Integer MOCK_SERVER_PORT = 7003;
    private final ClientAndServer clientAndServer;
    private final MockServerClient mockServerClient;

    public MockServerConfigCoursesService() {
        try {
            this.clientAndServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
            this.mockServerClient = new MockServerClient("localhost", MOCK_SERVER_PORT);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start MockServer", e);
        }
    }

    public void registerGetCourse1ByCourseIdEndpoint() {
        CourseResponseModel courseResponseModel =
                CourseResponseModel.builder()
                        .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                        .courseName("Web Services")
                        .courseNumber("N45-LA")
                        .department("Computer Science")
                        .numCredits(2.0)
                        .numHours(60)
                        .build();

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/courses/9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{\"courseId\":\"9a29fff7-564a-4cc9-8fe1-36f6ca9bc223\",\"courseName\":\"Web Services\",\"courseNumber\":\"N45-LA\",\"department\":\"Computer Science\",\"numCredits\":2.0,\"numHours\":60}", MediaType.APPLICATION_JSON))
                                .withDelay(TimeUnit.SECONDS, 3)
                );
    }

    public void registerGetCourse2ByCourseIdEndpoint() {
        CourseResponseModel courseResponseModel =
                CourseResponseModel.builder()
                        .courseId("8d764f78-8468-4769-b643-10cde392fbde")
                        .courseName("Waves")
                        .courseNumber("xud-857")
                        .department("Physics")
                        .numCredits(2.5)
                        .numHours(60)
                        .build();

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/courses/8d764f78-8468-4769-b643-10cde392fbde")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{\"courseId\":\"8d764f78-8468-4769-b643-10cde392fbde\",\"courseName\":\"Waves\",\"courseNumber\":\"xud-857\",\"department\":\"Physics\",\"numCredits\":2.5,\"numHours\":60}", MediaType.APPLICATION_JSON))
                                .withDelay(TimeUnit.SECONDS, 3)
                );
    }

    /*
    public void verifyGetCourse1ByCourseIdEndpoint() {
        // Log actual requests for debugging
        System.out.println("Verifying request with MockServer...");

        mockServerClient.verify(
                request()
                        .withMethod("GET")
                        .withPath("/api/v1/courses/9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                        .withBody(json("{\"courseId\":\"9a29fff7-564a-4cc9-8fe1-36f6ca9bc223\",\"courseName\":\"Web Services\",\"courseNumber\":\"N45-LA\",\"department\":\"Computer Science\",\"numCredits\":2.0,\"numHours\":60}", MediaType.APPLICATION_JSON))
        );
    }
*/

    public void registerGetCourse_NonExisting_ByCourseIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/courses/non-existing-id")
                )
                .respond(
                        response()
                                .withStatusCode(404) // Not Found
                                .withBody("{\"error\": \"Course not found\"}", MediaType.APPLICATION_JSON)
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    public void registerGetCourse_INVALID_ByCourseIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/courses/invalid-id")
                )
                .respond(
                        response()
                                .withStatusCode(422) // Unprocessable entity
                                .withBody("{\"error\": \"Invalid course ID format\"}", MediaType.APPLICATION_JSON)
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    public void stopServer() {
        if (clientAndServer != null) {
            clientAndServer.stop();
        }
    }
}
