package tech.me.direct.debit.service.mandate.callback;

import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;

public interface MandateCallbackService {
    void handleCallback(MandateCallbackRequest request);
} 