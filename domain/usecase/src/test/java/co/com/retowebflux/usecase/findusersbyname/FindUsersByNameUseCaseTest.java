package co.com.retowebflux.usecase.findusersbyname;

import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserCacheRepository;
import co.com.retowebflux.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUsersByNameUseCaseTest {

    @InjectMocks
    FindUsersByNameUseCase useCase;

    @Mock
    UserRepository repository;

    @Mock
    UserCacheRepository cacheRepository;

    @Test
    void executeHappyPath() {
        User user = User.builder().id(1L).firstName("George").lastName("Edwards").build();

        when(cacheRepository.findByFirstNameAndLastName("George", "Edwards"))
                .thenReturn(Mono.just(List.of(user)));

        StepVerifier.create(useCase.execute("George", "Edwards"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void executeSadPath() {
        StepVerifier.create(useCase.execute(" ", "Edwards"))
                .expectErrorMatches(e -> e instanceof BusinessException be
                        && be.getTechnicalMessage() == TechnicalMessage.INVALID_REQUEST)
                .verify();
    }
}
