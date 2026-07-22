package co.com.retowebflux.model.exception;

import co.com.retowebflux.model.enums.TechnicalMessage;

public class BusinessException extends ProcessorException {

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }
}
