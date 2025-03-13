package tech.me.direct.debit.service.mandate.complete.exception;

import lombok.Getter;

@Getter
public class ProviderNotFoundException extends RuntimeException {
    private static final String PROVIDER_NOT_FOUND_EXCEPTION = "PROVIDER_NOT_FOUND_EXCEPTION";
    private final Integer providerId;

    public ProviderNotFoundException(Integer providerId) {
        super(PROVIDER_NOT_FOUND_EXCEPTION);
        this.providerId = providerId;
    }
} 