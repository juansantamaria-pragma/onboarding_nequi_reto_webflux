package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

public interface UserProviderGateway {
    Mono<User> getUserById(Long id);
}
