package tech.me.direct.debit.service.mandate.exception;

import lombok.Getter;

@Getter
public class MandateNotFoundException extends RuntimeException {
    private static final String MANDATE_NOT_FOUND_EXCEPTION = "MANDATE_NOT_FOUND_EXCEPTION";
    private final String mandateId;

    public MandateNotFoundException(String mandateId) {
        super(MANDATE_NOT_FOUND_EXCEPTION);
        this.mandateId = mandateId;
    }
} 