package co.com.retowebflux.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private Long idReqRes;
    private String email;
    private String firstName;
    private String lastName;
}
