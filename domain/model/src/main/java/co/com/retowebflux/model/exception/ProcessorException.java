package co.com.retowebflux.model.exception;

import co.com.retowebflux.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public abstract class ProcessorException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    protected ProcessorException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    protected ProcessorException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }
}
