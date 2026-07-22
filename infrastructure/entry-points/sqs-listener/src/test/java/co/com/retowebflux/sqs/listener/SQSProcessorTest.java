package co.com.retowebflux.sqs.listener;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.usecase.saveuserevent.SaveUserEventUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSProcessorTest {

    @Mock
    SaveUserEventUseCase saveUserEventUseCase;

    final ObjectMapper objectMapper = new ObjectMapper();

    SQSProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SQSProcessor(saveUserEventUseCase, objectMapper);
    }

    @Test
    void applyHappyPath() {
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();
        Message message = Message.builder().body(objectMapper.writeValueAsString(user)).build();

        when(saveUserEventUseCase.execute(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(processor.apply(message))
                .verifyComplete();
    }

    @Test
    void applySadPath() {
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();
        Message message = Message.builder().body(objectMapper.writeValueAsString(user)).build();

        when(saveUserEventUseCase.execute(any(User.class))).thenReturn(Mono.error(new RuntimeException("save failed")));

        StepVerifier.create(processor.apply(message))
                .expectErrorMessage("save failed")
                .verify();
    }
}
