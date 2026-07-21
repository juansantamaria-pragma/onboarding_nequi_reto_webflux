package co.com.retowebflux.consumer;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.exception.TechnicalException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserProviderGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReqresUserConsumer implements UserProviderGateway {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "reqresGetUser")
    public Mono<User> getUserById(Integer id) {
        return client
                .get()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)))
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new TechnicalException(TechnicalMessage.EXTERNAL_SERVICE_ERROR)))
                .bodyToMono(ReqresUserResponse.class)
                .map(ReqresUserConsumer::toDomain)
                .onErrorMap(ex -> !(ex instanceof BusinessException) && !(ex instanceof TechnicalException),
                        ex -> new TechnicalException(ex, TechnicalMessage.EXTERNAL_SERVICE_ERROR));
    }

    private static User toDomain(ReqresUserResponse response) {
        ReqresUserData data = response.data();
        return User.builder()
                .id(data.id())
                .email(data.email())
                .firstName(data.firstName())
                .lastName(data.lastName())
                .build();
    }
}
