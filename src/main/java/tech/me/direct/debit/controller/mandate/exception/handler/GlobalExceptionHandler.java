package tech.me.direct.debit.controller.mandate.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.me.direct.debit.service.mandate.create.exception.UserNotFoundException;
import tech.me.direct.debit.service.mandate.exception.InvalidStateException;
import tech.me.direct.debit.service.mandate.exception.MandateCallbackInternalException;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.MandateNotInExpectedStatusException;
import tech.me.direct.debit.service.mandate.exception.ProviderNotFoundException;
import tech.me.direct.debit.service.mandate.exception.RedirectToProviderServiceInternalException;
import tech.me.direct.debit.service.provider.exception.ProviderRedirectInternalServiceException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getUserId());
    }

    @ExceptionHandler(MandateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMandateNotFoundException(MandateNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getMandateId());
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProviderNotFoundException(ProviderNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getProviderId().toString());
    }

    @ExceptionHandler(MandateNotInExpectedStatusException.class)
    public ResponseEntity<ErrorResponse> handleMandateNotInExpectedStatusException(
            MandateNotInExpectedStatusException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), 
                "mandateReferenceId: " + ex.getMandateReferenceId() + ", status: " + ex.getStatus());
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStateException(InvalidStateException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMandateReferenceId());
    }

    @ExceptionHandler({
        RedirectToProviderServiceInternalException.class,
        ProviderRedirectInternalServiceException.class,
        MandateCallbackInternalException.class
    })
    public ResponseEntity<ErrorResponse> handleInternalServiceExceptions(RuntimeException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status,
                                                              String message,
                                                              String details) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                details,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    public record ErrorResponse(
            int status,
            String error,
            String message,
            String details,
            LocalDateTime timestamp
    ) {}
} 