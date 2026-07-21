package co.com.retowebflux.model.exception;

import co.com.retowebflux.model.enums.TechnicalMessage;

public class TechnicalException extends ProcessorException {

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }
}
