package co.com.retowebflux.api.dto;

import co.com.retowebflux.model.enums.TechnicalMessage;

public record ErrorResponse(String code, String message, String param) {

    public static ErrorResponse from(TechnicalMessage technicalMessage) {
        return new ErrorResponse(technicalMessage.getCode(), technicalMessage.getMessage(), technicalMessage.getParam());
    }
}
