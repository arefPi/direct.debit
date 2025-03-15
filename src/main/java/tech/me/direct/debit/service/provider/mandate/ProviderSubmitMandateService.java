package tech.me.direct.debit.service.provider.mandate;

import tech.me.direct.debit.service.provider.ProviderService;
import tech.me.direct.debit.service.provider.mandate.impl.model.SubmitMandateRequest;

public interface ProviderSubmitMandateService extends ProviderService {
    void submit(SubmitMandateRequest request);
}
