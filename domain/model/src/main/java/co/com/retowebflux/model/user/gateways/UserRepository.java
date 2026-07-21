package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findByIdReqRes(Integer idReqRes);

    Mono<User> save(User user);
}
