package tech.me.direct.debit.service.mandate.submit;

import org.springframework.stereotype.Service;

@Service
public class SubmitMandateToMellatBankService implements SubmitMandateToProviderService {
    @Override
    public void submit(String mandateReferenceId) {
        // submit mandate to Mellat provider
    }
}
