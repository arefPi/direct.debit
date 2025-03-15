package tech.me.direct.debit.service.provider.mandate.impl.mellat.submit;

import org.springframework.stereotype.Service;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.provider.mandate.ProviderSubmitMandateService;
import tech.me.direct.debit.service.provider.mandate.impl.model.SubmitMandateRequest;

@Service
public class MellatBankSubmitMandateService implements ProviderSubmitMandateService {
    @Override
    public void submit(SubmitMandateRequest request) {
        // submit mandate to mellat
    }

    @Override
    public ProviderId providerId() {
        return ProviderId.BKMTIRTH;
    }
}
