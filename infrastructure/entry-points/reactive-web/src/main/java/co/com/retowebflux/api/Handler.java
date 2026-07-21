package co.com.retowebflux.api;

import co.com.retowebflux.api.dto.ResponseCreateUser;
import co.com.retowebflux.api.dto.ResponseGetUser;
import co.com.retowebflux.usecase.createuser.CreateUserUseCase;
import co.com.retowebflux.usecase.findallusers.FindAllUsersUseCase;
import co.com.retowebflux.usecase.finduserbyid.FindUserByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> Long.parseLong(serverRequest.pathVariable("id")))
                .flatMap(createUserUseCase::execute)
                .map(ResponseCreateUser::from)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        return Mono.fromCallable(() -> Long.parseLong(serverRequest.pathVariable("id")))
                .flatMap(findUserByIdUseCase::execute)
                .map(ResponseGetUser::from)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> getAllUsers (ServerRequest serverRequest) {
        return findAllUsersUseCase.execute()
                .map(ResponseGetUser::from)
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users));

    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }
}
