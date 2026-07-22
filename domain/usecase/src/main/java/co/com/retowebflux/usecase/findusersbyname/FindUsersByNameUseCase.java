package co.com.retowebflux.usecase.findusersbyname;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserCacheRepository;
import co.com.retowebflux.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUsersByNameUseCase {

    private final UserRepository repository;
    private final UserCacheRepository cacheRepository;

    public Flux<User> execute(String firstName) {
        return Mono.justOrEmpty(firstName)
                .filter(fn -> !fn.isBlank())
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)))
                .flatMapMany(this::searchWithCache);
    }

    private Flux<User> searchWithCache(String firstName) {
        return cacheRepository.findByFirstName(firstName)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.defer(() ->
                        repository.findByFirstName(firstName)
                                .collectList()
                                .filter(users -> !users.isEmpty())
                                .flatMap(users -> cacheRepository.save(firstName, users).thenReturn(users))
                                .flatMapMany(Flux::fromIterable)
                ));
    }
}
