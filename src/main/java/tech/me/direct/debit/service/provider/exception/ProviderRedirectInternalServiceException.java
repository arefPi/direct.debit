package tech.me.direct.debit.service.provider.exception;

import lombok.Getter;

@Getter
public class ProviderRedirectInternalServiceException extends RuntimeException {
    private static final String PROVIDER_REDIRECT_INTERNAL_SERVICE_EXCEPTION = "PROVIDER_REDIRECT_INTERNAL_SERVICE_EXCEPTION";

    public ProviderRedirectInternalServiceException(Exception cause) {
        super(PROVIDER_REDIRECT_INTERNAL_SERVICE_EXCEPTION, cause);
    }
} 