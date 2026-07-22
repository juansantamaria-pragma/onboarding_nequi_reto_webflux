package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserCacheRepository {
    Mono<List<User>> findByFirstName(String firstName);

    Mono<Void> save(String firstName, List<User> users);
}
