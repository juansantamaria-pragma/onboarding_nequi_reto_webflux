package co.com.retowebflux.usecase.saveuserevent;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserNoSqlRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaveUserEventUseCase {

    private final UserNoSqlRepository userNoSqlRepository;

    public Mono<User> execute(User user) {
        return userNoSqlRepository.findById(user.getId())
                .switchIfEmpty(Mono.defer(() -> userNoSqlRepository.save(user)));
    }
}
