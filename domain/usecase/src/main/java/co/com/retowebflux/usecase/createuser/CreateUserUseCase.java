package co.com.retowebflux.usecase.createuser;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserProviderGateway;
import co.com.retowebflux.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final UserProviderGateway userProviderGateway;

    public Mono<User> execute(Integer id) {
        return Mono.justOrEmpty(id)
                .filter(validId -> validId > 0)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_USER_ID)))
                .flatMap(userRepository::findById)
                .switchIfEmpty(Mono.defer(() -> userProviderGateway.getUserById(id)
                        .flatMap(userRepository::save)));
    }
}
