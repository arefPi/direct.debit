package tech.me.direct.debit.service.mandate.exception;

public class RedirectToProviderServiceInternalException extends RuntimeException {
    private static final String REDIRECT_TO_PROVIDER_SERVICE_INTERNAL_EXCEPTION =
            "REDIRECT_TO_PROVIDER_SERVICE_INTERNAL_EXCEPTION";

    public RedirectToProviderServiceInternalException() {
        super(REDIRECT_TO_PROVIDER_SERVICE_INTERNAL_EXCEPTION);
    }
}
