package co.com.retowebflux.redis;

import co.com.retowebflux.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRedisAdapterTest {

    @Mock
    ReactiveRedisConnectionFactory connectionFactory;

    @Mock
    ObjectMapper mapper;

    @Mock
    ReactiveRedisTemplate<String, UserSearchCache> template;

    @Mock
    ReactiveValueOperations<String, UserSearchCache> valueOps;

    UserRedisAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRedisAdapter(connectionFactory, mapper, 600L);
        ReflectionTestUtils.setField(adapter, "template", template);
        when(template.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void findByFirstNameHappyPath() {
        User user = User.builder().id(1L).firstName("George").lastName("Edwards").build();
        UserSearchCache cache = new UserSearchCache(List.of(user));

        when(valueOps.get("user:search:george")).thenReturn(Mono.just(cache));
        when(mapper.map(cache, UserSearchCache.class)).thenReturn(cache);

        StepVerifier.create(adapter.findByFirstName("George"))
                .expectNext(List.of(user))
                .verifyComplete();
    }

    @Test
    void saveSadPath() {
        UserSearchCache cache = new UserSearchCache(List.of());

        when(mapper.map(any(UserSearchCache.class), eq(UserSearchCache.class))).thenReturn(cache);
        when(valueOps.set(anyString(), eq(cache))).thenReturn(Mono.error(new RuntimeException("redis down")));

        StepVerifier.create(adapter.save("George", List.of()))
                .expectErrorMessage("redis down")
                .verify();
    }
}
