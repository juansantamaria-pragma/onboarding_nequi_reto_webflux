package co.com.retowebflux.usecase.saveuserevent;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserNoSqlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveUserEventUseCaseTest {

    @InjectMocks
    SaveUserEventUseCase useCase;

    @Mock
    UserNoSqlRepository userNoSqlRepository;

    @Test
    void executeHappyPath() {
        User user = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();

        when(userNoSqlRepository.findById(1L)).thenReturn(Mono.empty());
        when(userNoSqlRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.execute(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void executeSadPath() {
        User user = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();

        when(userNoSqlRepository.findById(1L)).thenReturn(Mono.empty());
        when(userNoSqlRepository.save(user)).thenReturn(Mono.error(new RuntimeException("dynamo down")));

        StepVerifier.create(useCase.execute(user))
                .expectErrorMessage("dynamo down")
                .verify();
    }
}
