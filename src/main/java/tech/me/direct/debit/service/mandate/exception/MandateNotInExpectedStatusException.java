package tech.me.direct.debit.service.mandate.exception;

import lombok.Getter;

@Getter
public class MandateNotInExpectedStatusException extends RuntimeException {
    private static final String MANDATE_NOT_IN_EXPECTED_STATUS_EXCEPTION = "MANDATE_NOT_IN_EXPECTED_STATUS_EXCEPTION";
    private final String mandateReferenceId;
    private final String status;

    public MandateNotInExpectedStatusException(String mandateReferenceId, String status) {
        super(MANDATE_NOT_IN_EXPECTED_STATUS_EXCEPTION);
        this.mandateReferenceId = mandateReferenceId;
        this.status = status;
    }
}
