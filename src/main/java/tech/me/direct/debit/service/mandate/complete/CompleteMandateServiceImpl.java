package tech.me.direct.debit.service.mandate.complete;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.config.properties.MandateProperties;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.ProviderRepository;
import tech.me.direct.debit.service.mandate.complete.mapper.CompleteMandateMapper;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.MandateNotInExpectedStatusException;
import tech.me.direct.debit.service.mandate.exception.ProviderNotFoundException;

@Service
@RequiredArgsConstructor
public class CompleteMandateServiceImpl implements CompleteMandateService {
    private final MandateRepository mandateRepository;
    private final ProviderRepository providerRepository;
    private final MandateProperties mandateProperties;
    private final CompleteMandateMapper completeMandateMapper;

    @Override
    public void completeMandate(CompleteMandateRequest request) {
        final var mandateReferenceId = request.mandateReferenceId();
        final var mandate = mandateRepository.findByReferenceId(mandateReferenceId)
                .orElseThrow(() -> new MandateNotFoundException(mandateReferenceId));

        if (MandateStatus.INITIAL != mandate.getStatus()) {
            throw new MandateNotInExpectedStatusException(mandateReferenceId, mandate.getStatus().name());
        }

        final var provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ProviderNotFoundException(request.providerId()));

        final var defaultExpiryDays = mandateProperties.getDefaultExpiryDays();

        completeMandateMapper.updateMandate(
                mandate,
                provider,
                request,
                MandateStatus.DRAFT,
                defaultExpiryDays);

        mandateRepository.save(mandate);
    }
}
