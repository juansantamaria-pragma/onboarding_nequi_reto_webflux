package co.com.retowebflux.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, Integer> {
}
