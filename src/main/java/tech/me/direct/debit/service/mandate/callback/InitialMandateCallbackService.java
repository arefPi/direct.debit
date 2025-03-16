package tech.me.direct.debit.service.mandate.callback;

import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;

public interface InitialMandateCallbackService {
    Mandate handleInitialCallback(MandateCallbackRequest request);
} 