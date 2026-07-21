package co.com.retowebflux.r2dbc;

import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserRepository;
import co.com.retowebflux.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    Integer,
    UserReactiveRepository
> implements UserRepository {
    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }
}
