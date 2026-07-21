package co.com.retowebflux.sqs.sender;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserEventPublisherGateway;
import co.com.retowebflux.sqs.sender.config.SQSSenderProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import tools.jackson.databind.ObjectMapper;

@Service
@Log4j2
public class SQSSender implements UserEventPublisherGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public SQSSender(SQSSenderProperties properties, @Qualifier("senderSqsClient") SqsAsyncClient client, ObjectMapper objectMapper) {
        this.properties = properties;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    @Override
    public Mono<Void> publish(User user) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(user))
                .flatMap(this::send)
                .then();
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}
