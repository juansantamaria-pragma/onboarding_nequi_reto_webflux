package co.com.retowebflux.api.exceptionhandler;

import co.com.retowebflux.api.dto.ErrorResponse;
import co.com.retowebflux.model.enums.TechnicalMessage;
import co.com.retowebflux.model.exception.ProcessorException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        TechnicalMessage technicalMessage = ex instanceof ProcessorException processorException
                ? processorException.getTechnicalMessage()
                : TechnicalMessage.INTERNAL_ERROR;

        return ServerResponse.status(HttpStatus.valueOf(Integer.parseInt(technicalMessage.getCode())))
                .bodyValue(ErrorResponse.from(technicalMessage))
                .flatMap(response -> response.writeTo(exchange, new ResponseContext()));
    }

    private static class ResponseContext implements ServerResponse.Context {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return HandlerStrategies.withDefaults().messageWriters();
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return HandlerStrategies.withDefaults().viewResolvers();
        }
    }
}
