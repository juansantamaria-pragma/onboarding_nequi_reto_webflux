package co.com.retowebflux.api.config;

import co.com.retowebflux.api.Handler;
import co.com.retowebflux.api.RouterRest;
import co.com.retowebflux.usecase.createuser.CreateUserUseCase;
import co.com.retowebflux.usecase.findallusers.FindAllUsersUseCase;
import co.com.retowebflux.usecase.finduserbyid.FindUserByIdUseCase;
import co.com.retowebflux.usecase.findusersbyname.FindUsersByNameUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

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

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(findAllUsersUseCase.execute()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}