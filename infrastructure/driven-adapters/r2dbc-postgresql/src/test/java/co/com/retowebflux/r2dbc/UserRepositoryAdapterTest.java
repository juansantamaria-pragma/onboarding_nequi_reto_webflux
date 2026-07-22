package co.com.retowebflux.r2dbc;

import co.com.retowebflux.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {
        UserEntity entity = UserEntity.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").build();
        User user = User.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        UserEntity entity = UserEntity.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").build();
        User user = User.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").build();

        when(mapper.map(user, UserEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(1L))
                .verifyComplete();
    }
}
