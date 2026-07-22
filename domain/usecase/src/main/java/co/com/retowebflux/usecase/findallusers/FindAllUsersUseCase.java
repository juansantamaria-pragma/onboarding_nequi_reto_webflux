package co.com.retowebflux.usecase.findallusers;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FindAllUsersUseCase {

    private final UserRepository repository;

    public Flux<User> execute (){
        return repository.findAll();

    }
}
