package co.com.retowebflux.usecase.finduserbyid;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUserByIdUseCase {

    private final UserRepository repository;

    public Mono<User> execute(Long id) {
        return Mono.justOrEmpty(id)
                .filter(validId -> validId > 0)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_USER_ID)))
                .flatMap(repository::findById)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)));
    }
}
