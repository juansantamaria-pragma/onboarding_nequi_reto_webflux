package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findByIdReqRes(Long idReqRes);

    Mono<User> findById(Long id);

    Flux<User> findAll();

    Flux<User> findByFirstName(String firstName);

    Mono<User> save(User user);
}
