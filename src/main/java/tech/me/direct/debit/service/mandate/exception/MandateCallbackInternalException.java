package tech.me.direct.debit.service.mandate.exception;

import lombok.Getter;

@Getter
public class MandateCallbackInternalException extends RuntimeException {
    private static final String MANDATE_CALLBACK_INTERNAL_EXCEPTION = "MANDATE_CALLBACK_INTERNAL_EXCEPTION";
    private final String error;

    public MandateCallbackInternalException(String error) {
        super(MANDATE_CALLBACK_INTERNAL_EXCEPTION);
        this.error = error;
    }

    public MandateCallbackInternalException(String error, Throwable cause) {
        super(MANDATE_CALLBACK_INTERNAL_EXCEPTION, cause);
        this.error = error;
    }
} 