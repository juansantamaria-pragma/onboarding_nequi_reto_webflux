package co.com.retowebflux.usecase.createuser;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserEventPublisherGateway;
import co.com.retowebflux.model.user.gateways.UserProviderGateway;
import co.com.retowebflux.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserProviderGateway userProviderGateway;
    private final UserEventPublisherGateway userEventPublisherGateway;

    public Mono<User> execute(Long idReqRes) {
        return Mono.justOrEmpty(idReqRes)
                .filter(validId -> validId > 0)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_USER_ID)))
                .flatMap(userRepository::findByIdReqRes)
                .switchIfEmpty(Mono.defer(() -> userProviderGateway.getUserById(idReqRes)
                        .flatMap(userRepository::save)))
                .flatMap(user -> userEventPublisherGateway.publish(user).thenReturn(user));
    }
}
