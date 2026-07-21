package co.com.retowebflux.api.dto;

import co.com.retowebflux.model.user.User;

public record ResponseCreateUser(Integer id, String email, String firstName, String lastName) {

    public static ResponseCreateUser from(User user) {
        return new ResponseCreateUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
