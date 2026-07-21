package co.com.retowebflux.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReqresUserData(
        Integer id,
        String email,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName) {
}
