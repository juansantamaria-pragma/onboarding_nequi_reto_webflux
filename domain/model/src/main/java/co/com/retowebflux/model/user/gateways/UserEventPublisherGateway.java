package co.com.retowebflux.model.user.gateways;

import co.com.retowebflux.model.user.User;
import reactor.core.publisher.Mono;

public interface UserEventPublisherGateway {
    Mono<Void> publish(User user);
}
