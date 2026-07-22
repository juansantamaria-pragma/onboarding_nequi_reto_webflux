package co.com.retowebflux.usecase.finduserbyid;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseTest {

    @InjectMocks
    FindUserByIdUseCase useCase;

    @Mock
    UserRepository repository;

    @Test
    void executeHappyPath() {
        User user = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();

        when(repository.findById(1L)).thenReturn(Mono.just(user));

        StepVerifier.create(useCase.execute(1L))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void executeSadPath() {
        when(repository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L))
                .expectErrorMatches(e -> e instanceof BusinessException be
                        && be.getTechnicalMessage() == TechnicalMessage.USER_NOT_FOUND)
                .verify();
    }
}
