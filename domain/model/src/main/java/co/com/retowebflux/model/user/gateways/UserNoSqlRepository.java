package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

public interface UserNoSqlRepository {
    Mono<User> findById(Long id);

    Mono<User> save(User user);
}
