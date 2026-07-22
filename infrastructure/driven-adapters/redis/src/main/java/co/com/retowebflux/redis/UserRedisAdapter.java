package co.com.retowebflux.redis;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserCacheRepository;
import co.com.retowebflux.redis.template.helper.ReactiveTemplateAdapterOperations;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Component
public class UserRedisAdapter extends ReactiveTemplateAdapterOperations<UserSearchCache, String, UserSearchCache>
        implements UserCacheRepository {

    private final long ttlMillis;

    public UserRedisAdapter(ReactiveRedisConnectionFactory connectionFactory, ObjectMapper mapper,
                             @Value("${adapters.redis.ttl-seconds}") long ttlSeconds) {
        super(connectionFactory, mapper, d -> mapper.map(d, UserSearchCache.class));
        this.ttlMillis = ttlSeconds * 1000;
    }

    @Override
    public Mono<List<User>> findByFirstName(String firstName) {
        String key = buildKey(firstName);
        return findById(key)
                .doOnNext(cache -> log.info("Redis cache HIT key={} size={}", key, cache.getUsers().size()))
                .map(UserSearchCache::getUsers);
    }

    @Override
    public Mono<Void> save(String firstName, List<User> users) {
        return save(buildKey(firstName), new UserSearchCache(users), ttlMillis).then();
    }

    private String buildKey(String firstName) {
        return "user:search:%s".formatted(firstName.toLowerCase());
    }
}
