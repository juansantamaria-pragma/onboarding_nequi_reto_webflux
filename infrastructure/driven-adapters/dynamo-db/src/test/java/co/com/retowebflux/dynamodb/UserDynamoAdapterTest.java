package co.com.retowebflux.dynamodb;

import co.com.retowebflux.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDynamoAdapterTest {

    @Mock
    DynamoDbEnhancedAsyncClient client;

    @Mock
    DynamoDbAsyncTable<UserDynamoEntity> table;

    @Mock
    ObjectMapper mapper;

    UserDynamoAdapter adapter;

    @BeforeEach
    void setUp() {
        when(client.table(eq("users"), any(TableSchema.class))).thenReturn(table);
        adapter = new UserDynamoAdapter(client, mapper);
    }

    @Test
    void findByIdHappyPath() {
        UserDynamoEntity entity = new UserDynamoEntity("1", "a@a.com", "A", "B");
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();

        when(table.getItem(any(Key.class))).thenReturn(CompletableFuture.completedFuture(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void saveSadPath() {
        User user = User.builder().id(1L).email("a@a.com").firstName("A").lastName("B").build();
        UserDynamoEntity entity = new UserDynamoEntity("1", "a@a.com", "A", "B");

        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("dynamo down"));

        when(mapper.map(user, UserDynamoEntity.class)).thenReturn(entity);
        when(table.putItem(entity)).thenReturn(failedFuture);

        StepVerifier.create(adapter.save(user))
                .expectErrorMessage("dynamo down")
                .verify();
    }
}
