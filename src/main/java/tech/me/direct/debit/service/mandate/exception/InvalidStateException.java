package tech.me.direct.debit.service.mandate.exception;

import lombok.Getter;

@Getter
public class InvalidStateException extends RuntimeException {
    private static final String INVALID_STATE_EXCEPTION = "INVALID_STATE_EXCEPTION";
    private final String mandateReferenceId;

    public InvalidStateException(String mandateReferenceId) {
        super(INVALID_STATE_EXCEPTION);
        this.mandateReferenceId = mandateReferenceId;
    }
} 