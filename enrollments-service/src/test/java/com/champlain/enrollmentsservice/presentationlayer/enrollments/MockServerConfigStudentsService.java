package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class MockServerConfigStudentsService{

    private static final Integer MOCK_SERVER_PORT = 7002;
    private final ClientAndServer clientAndServer;
    private final MockServerClient mockServerClient = new
            MockServerClient("localhost", MOCK_SERVER_PORT);

    public MockServerConfigStudentsService() {
        this.clientAndServer =
                ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    public void registerGetStudent1ByStudentIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/c3540a89-cb47-4c96-888e-ff96708db4d8")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{\"studentId\":\"c3540a89-cb47-4c96-888e-ff96708db4d8\",\"firstName\":\"Donna\",\"lastName\":\"Hornsby\",\"program\":\"History\",\"stuff\":\"stuff\"}", MediaType.APPLICATION_JSON))
                                .withDelay(TimeUnit.SECONDS, 3)
                );
    }


    public void registerGetStudent2ByStudentIdEndpoint() {
        StudentResponseModel studentResponseModel =
                StudentResponseModel.builder()
                        .studentId("1f538db7-320a-4415-bad4-e1d44518b1ff")
                        .firstName("Willis")
                        .lastName("Faraday")
                        .program("Pure and Applied Sciences")
                        .stuff("stuff")
                        .build();

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/1f538db7-320a-4415-bad4-e1d44518b1ff")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(json("{\"studentId\":\"1f538db7-320a-4415-bad4-e1d44518b1ff\",\"firstName\":\"Willis\",\"lastName\":\"Faraday\",\"program\":\"Pure and Applied Sciences\",\"stuff\":\"stuff\"}", MediaType.APPLICATION_JSON))
                                .withDelay(TimeUnit.SECONDS, 3)
                );
    }

/*
    public void verifyGetStudent1ByStudentIdEndpoint() {
        mockServerClient.verify(
                request()
                        .withMethod("GET")
                        .withPath("/api/v1/students/c3540a89-cb47-4c96-888e-ff96708db4d8")
                        .withBody(json("{\"studentId\":\"c3540a89-cb47-4c96-888e-ff96708db4d8\",\"firstName\":\"Donna\",\"lastName\":\"Hornsby\",\"program\":\"History\",\"stuff\":\"stuff\"}", MediaType.APPLICATION_JSON))
        );
    }
*/

    public void registerGetStudent_NonExisting_ByStudentIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/non-existing-id")
                )
                .respond(
                        response()
                                .withStatusCode(404) // Not Found
                                .withBody("{\"error\": \"Student not found\"}", MediaType.APPLICATION_JSON)
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    public void registerGetStudent_INVALID_ByStudentIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/invalid-id")
                )
                .respond(
                        response()
                                .withStatusCode(422) // Unprocessable entity
                                .withBody("{\"error\": \"Invalid student ID format\"}", MediaType.APPLICATION_JSON)
                                .withDelay(TimeUnit.SECONDS, 1)
                );
    }

    public void stopServer() {
        if (clientAndServer != null)
            clientAndServer.stop();
    }

}