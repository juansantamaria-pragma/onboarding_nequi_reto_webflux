package co.com.retowebflux.usecase.createuser;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserEventPublisherGateway;
import co.com.retowebflux.model.user.gateways.UserProviderGateway;
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
class CreateUserUseCaseTest {

    @InjectMocks
    CreateUserUseCase useCase;

    @Mock
    UserRepository userRepository;

    @Mock
    UserProviderGateway userProviderGateway;

    @Mock
    UserEventPublisherGateway userEventPublisherGateway;

    @Test
    void executeHappyPath() {
        User providerUser = User.builder().idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();
        User savedUser = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();

        when(userRepository.findByIdReqRes(1L)).thenReturn(Mono.empty());
        when(userProviderGateway.getUserById(1L)).thenReturn(Mono.just(providerUser));
        when(userRepository.save(providerUser)).thenReturn(Mono.just(savedUser));
        when(userEventPublisherGateway.publish(savedUser)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    void executeSadPath() {
        StepVerifier.create(useCase.execute(0L))
                .expectErrorMatches(e -> e instanceof BusinessException be
                        && be.getTechnicalMessage() == TechnicalMessage.INVALID_USER_ID)
                .verify();
    }
}
