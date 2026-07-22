package co.com.retowebflux.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReqresUserData(
        Long id,
        String email,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName) {
}
