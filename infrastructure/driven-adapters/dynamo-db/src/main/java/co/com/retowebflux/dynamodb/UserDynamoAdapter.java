package co.com.retowebflux.dynamodb;

import co.com.retowebflux.dynamodb.helper.TemplateAdapterOperations;
import co.com.retowebflux.model.user.User;
import co.com.retowebflux.model.user.gateways.UserNoSqlRepository;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
@Log4j2
public class UserDynamoAdapter extends TemplateAdapterOperations<User, String, UserDynamoEntity> implements UserNoSqlRepository {

    public UserDynamoAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, entity -> mapper.map(entity, User.class), "users");
    }

    @Override
    public Mono<User> findById(Integer id) {
        return getById(String.valueOf(id));
    }

    @Override
    public Mono<User> save(User user) {
        return super.save(user)
                .doOnNext(saved -> log.info("Usuario guardado en DynamoDB con id {}", saved.getId()));
    }
}
