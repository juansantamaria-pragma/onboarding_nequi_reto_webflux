package co.com.retowebflux.usecase.findallusers;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @InjectMocks
    FindAllUsersUseCase useCase;

    @Mock
    UserRepository repository;

    @Test
    void executeHappyPath() {
        User user1 = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();
        User user2 = User.builder().id(2L).idReqRes(2L).email("c@c.com").firstName("C").lastName("D").build();

        when(repository.findAll()).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(useCase.execute())
                .expectNext(user1, user2)
                .verifyComplete();
    }

    @Test
    void executeSadPath() {
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("db down")));

        StepVerifier.create(useCase.execute())
                .expectErrorMessage("db down")
                .verify();
    }
}
