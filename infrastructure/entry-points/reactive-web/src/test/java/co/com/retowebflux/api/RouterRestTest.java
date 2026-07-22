package co.com.retowebflux.api;

import co.com.retowebflux.api.dto.RequestCreateUser;
import co.com.retowebflux.api.exceptionhandler.GlobalErrorHandler;
import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.usecase.createuser.CreateUserUseCase;
import co.com.retowebflux.usecase.findallusers.FindAllUsersUseCase;
import co.com.retowebflux.usecase.finduserbyid.FindUserByIdUseCase;
import co.com.retowebflux.usecase.findusersbyname.FindUsersByNameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalErrorHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private FindAllUsersUseCase findAllUsersUseCase;

    @MockitoBean
    private FindUsersByNameUseCase findUsersByNameUseCase;

    @BeforeEach
    void increaseResponseTimeout() {
        webTestClient = webTestClient.mutate().responseTimeout(Duration.ofSeconds(20)).build();
    }

    @Test
    void createUserHappyPath() {
        User user = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();
        when(createUserUseCase.execute(1L)).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RequestCreateUser(1L))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.idReqRes").isEqualTo(1);
    }

    @Test
    void createUserSadPath() {
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("400");
    }

    @Test
    void getUserHappyPath() {
        User user = User.builder().id(1L).idReqRes(1L).email("a@a.com").firstName("A").lastName("B").build();
        when(findUserByIdUseCase.execute(1L)).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/v1/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("A");
    }

    @Test
    void getUserSadPath() {
        when(findUserByIdUseCase.execute(99L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)));

        webTestClient.get()
                .uri("/api/v1/users/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("404")
                .jsonPath("$.param").isEqualTo("id");
    }
}
