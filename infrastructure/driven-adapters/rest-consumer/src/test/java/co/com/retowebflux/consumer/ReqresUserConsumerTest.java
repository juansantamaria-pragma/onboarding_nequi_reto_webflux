package co.com.retowebflux.consumer;

import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.exception.TechnicalException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class ReqresUserConsumerTest {

    private static ReqresUserConsumer reqresUserConsumer;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        reqresUserConsumer = new ReqresUserConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Maps a successful reqres response to the domain User")
    void getUserByIdSuccess() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"data\":{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\"}}"));

        var response = reqresUserConsumer.getUserById(2);

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getId().equals(2)
                        && user.getEmail().equals("janet.weaver@reqres.in")
                        && user.getFirstName().equals("Janet")
                        && user.getLastName().equals("Weaver"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Maps a 404 reqres response to BusinessException")
    void getUserByIdNotFound() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

        var response = reqresUserConsumer.getUserById(999);

        StepVerifier.create(response)
                .expectErrorMatches(BusinessException.class::isInstance)
                .verify();
    }

    @Test
    @DisplayName("Maps a 500 reqres response to TechnicalException")
    void getUserByIdExternalFailure() {
        mockBackEnd.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        var response = reqresUserConsumer.getUserById(2);

        StepVerifier.create(response)
                .expectErrorMatches(TechnicalException.class::isInstance)
                .verify();
    }
}
