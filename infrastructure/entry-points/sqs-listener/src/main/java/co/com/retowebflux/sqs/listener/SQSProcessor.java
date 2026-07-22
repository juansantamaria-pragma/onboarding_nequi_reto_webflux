package co.com.retowebflux.sqs.listener;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.usecase.saveuserevent.SaveUserEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;
import tools.jackson.databind.ObjectMapper;

import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final SaveUserEventUseCase saveUserEventUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), User.class))
                .doOnNext(user -> log.info(user.toString().toUpperCase()))
                .flatMap(saveUserEventUseCase::execute)
                .then();
    }
}
