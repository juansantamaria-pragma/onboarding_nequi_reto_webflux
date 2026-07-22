package co.com.retowebflux.api.dto;

import co.com.retowebflux.model.user.User;

public record ResponseGetUser(Long id, Long idReqRes, String email, String firstName, String lastName) {

    public static ResponseGetUser from(User user) {
        return new ResponseGetUser(user.getId(), user.getIdReqRes(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
