package tech.me.direct.debit.service.mandate.callback;

import tech.me.direct.debit.persistence.mandate.Mandate;

public interface TokenMandateCallbackService {
    void handleTokenCallback(Mandate mandate, String code);
} 