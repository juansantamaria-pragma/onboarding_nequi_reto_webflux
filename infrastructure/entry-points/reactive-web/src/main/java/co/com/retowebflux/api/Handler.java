package co.com.retowebflux.api;

import co.com.retowebflux.api.dto.RequestCreateUser;
import co.com.retowebflux.api.dto.ResponseCreateUser;
import co.com.retowebflux.api.dto.ResponseGetUser;
import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.BusinessException;
import co.com.retowebflux.usecase.createuser.CreateUserUseCase;
import co.com.retowebflux.usecase.findallusers.FindAllUsersUseCase;
import co.com.retowebflux.usecase.finduserbyid.FindUserByIdUseCase;
import co.com.retowebflux.usecase.findusersbyname.FindUsersByNameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class Handler {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final FindUsersByNameUseCase findUsersByNameUseCase;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        String traceId = UUID.randomUUID().toString();
        log.info("traceId={} request=CREATE_USER", traceId);
        return serverRequest.bodyToMono(RequestCreateUser.class)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)))
                .map(RequestCreateUser::idReqRes)
                .flatMap(createUserUseCase::execute)
                .doOnNext(user -> log.info("traceId={} step=USER_CREATED idReqRes={}", traceId, user.getIdReqRes()))
                .map(ResponseCreateUser::from)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .doOnError(error -> log.error("traceId={} error={}", traceId, error.toString()));
    }

    public Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        String traceId = UUID.randomUUID().toString();
        log.info("traceId={} request=GET_USER", traceId);
        return Mono.fromCallable(() -> Long.parseLong(serverRequest.pathVariable("id")))
                .flatMap(findUserByIdUseCase::execute)
                .doOnNext(user -> log.info("traceId={} step=USER_FOUND idReqRes={}", traceId, user.getIdReqRes()))
                .map(ResponseGetUser::from)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .doOnError(error -> log.error("traceId={} error={}", traceId, error.toString()));
    }

    public Mono<ServerResponse> getAllUsers (ServerRequest serverRequest) {
        String traceId = UUID.randomUUID().toString();
        log.info("traceId={} request=LIST_USERS", traceId);
        return findAllUsersUseCase.execute()
                .map(ResponseGetUser::from)
                .collectList()
                .doOnNext(users -> log.info("traceId={} step=USERS_LISTED count={}", traceId, users.size()))
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .doOnError(error -> log.error("traceId={} error={}", traceId, error.toString()));

    }

    public Mono<ServerResponse> searchUsersByName(ServerRequest serverRequest) {
        String traceId = UUID.randomUUID().toString();
        String firstName = serverRequest.queryParam("firstName").orElse(null);
        log.info("traceId={} request=SEARCH_USERS firstName={}", traceId, firstName);
        return findUsersByNameUseCase.execute(firstName)
                .map(ResponseGetUser::from)
                .collectList()
                .doOnNext(users -> log.info("traceId={} step=USERS_FOUND count={}", traceId, users.size()))
                .flatMap(users -> ServerResponse.ok().bodyValue(users))
                .doOnError(error -> log.error("traceId={} error={}", traceId, error.toString()));
    }
}
