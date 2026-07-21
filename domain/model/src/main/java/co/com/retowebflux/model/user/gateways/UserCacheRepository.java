package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserCacheRepository {
    Mono<List<User>> findByFirstNameAndLastName(String firstName, String lastName);

    Mono<Void> save(String firstName, String lastName, List<User> users);
}
