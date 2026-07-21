package co.com.retowebflux.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Something went wrong, please try again", ""),
    SERVICE_UNAVAILABLE("503", "Service temporarily unavailable, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_USER_ID("400", "User id must be a positive number", "id"),
    USER_NOT_FOUND("404", "User not found", "id"),
    EXTERNAL_SERVICE_ERROR("502", "Error calling external user provider", "");

    private final String code;
    private final String message;
    private final String param;
}
