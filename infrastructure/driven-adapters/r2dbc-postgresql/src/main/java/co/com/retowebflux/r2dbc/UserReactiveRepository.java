package co.com.retowebflux.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, Integer> {
    Mono<UserEntity> findByIdReqRes(Integer idReqRes);
}
