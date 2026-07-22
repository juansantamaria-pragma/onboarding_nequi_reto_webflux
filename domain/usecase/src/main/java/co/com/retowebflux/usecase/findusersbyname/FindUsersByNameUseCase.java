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

    public Flux<User> execute(String firstName, String lastName) {
        return Mono.justOrEmpty(firstName)
                .filter(fn -> !fn.isBlank())
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)))
                .flatMap(fn -> Mono.justOrEmpty(lastName)
                        .filter(ln -> !ln.isBlank())
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST))))
                .flatMapMany(ln -> searchWithCache(firstName, lastName));
    }

    private Flux<User> searchWithCache(String firstName, String lastName) {
        return cacheRepository.findByFirstNameAndLastName(firstName, lastName)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.defer(() ->
                        repository.findByFirstNameAndLastName(firstName, lastName)
                                .collectList()
                                .filter(users -> !users.isEmpty())
                                .flatMap(users -> cacheRepository.save(firstName, lastName, users).thenReturn(users))
                                .flatMapMany(Flux::fromIterable)
                ));
    }
}
