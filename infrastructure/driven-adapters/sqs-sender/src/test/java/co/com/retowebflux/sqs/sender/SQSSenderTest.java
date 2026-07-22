package co.com.retowebflux.sqs.sender;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.sqs.sender.config.SQSSenderProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    SqsAsyncClient client;

    SQSSenderProperties properties = new SQSSenderProperties("us-east-1", "http://localhost:4566/000000000000/sample", "http://localhost:4566");
    ObjectMapper objectMapper = new ObjectMapper();

    SQSSender sender;

    @BeforeEach
    void setUp() {
        sender = new SQSSender(properties, client, objectMapper);
    }

    @Test
    void publishHappyPath() {
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();
        SendMessageResponse response = SendMessageResponse.builder().messageId("m-1").build();

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(sender.publish(user))
                .verifyComplete();
    }

    @Test
    void publishSadPath() {
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();
        CompletableFuture<SendMessageResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("sqs down"));

        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(failedFuture);

        StepVerifier.create(sender.publish(user))
                .expectErrorMessage("sqs down")
                .verify();
    }
}
