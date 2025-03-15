package tech.me.direct.debit.service.mandate.redirect.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.MandateNotInExpectedStatusException;
import tech.me.direct.debit.service.mandate.exception.RedirectToProviderServiceInternalException;
import tech.me.direct.debit.service.mandate.redirect.RedirectToProviderService;
import tech.me.direct.debit.service.mandate.redirect.mapper.MandateMapper;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderResponse;
import tech.me.direct.debit.service.provider.mandate.ProviderRedirectService;
import tech.me.direct.debit.service.provider.mandate.impl.model.redirect.RedirectRequest;
import tech.me.direct.debit.util.ObjectResolver;

@RequiredArgsConstructor
@Service
public class RedirectToProviderServiceImpl implements RedirectToProviderService {
    private final MandateRepository mandateRepository;
    private final ObjectResolver<ProviderId, ProviderRedirectService> providerRedirectServiceResolver;
    private final MandateMapper mandateMapper;

    @Override
    public final RedirectToProviderResponse redirect(RedirectToProviderRequest request) {
        final var mandateReferenceId = request.mandateReferenceId();

        final var mandate = getMandate(mandateReferenceId);

        validateMandateStatus(mandate);

        final var providerId = mandate.getProvider().getProviderId();

        final var providerRedirectService =
                providerRedirectServiceResolver.resolve(providerId)
                        .orElseThrow(RedirectToProviderServiceInternalException::new);

        final var redirectRequest = creatRedirectRequest(mandate);
        final var redirectResponse = providerRedirectService.redirect(redirectRequest);

        changeMandateStatusToSent(mandate);

        return new RedirectToProviderResponse(redirectResponse.redirectUrl());
    }

    private Mandate getMandate(String mandateReferenceId) {
        return mandateRepository.findByReferenceId(mandateReferenceId)
                .orElseThrow(() -> new MandateNotFoundException(mandateReferenceId));
    }

    private void validateMandateStatus(Mandate mandate) {
        if (!MandateStatus.DRAFT.equals(mandate.getStatus())) {
            final var status = mandate.getStatus()
                    != null ? mandate.getStatus().name() : null;
            throw new MandateNotInExpectedStatusException(mandate.getReferenceId(), status);
        }
    }

    private void changeMandateStatusToSent(Mandate mandate) {
        mandate.setStatus(MandateStatus.SENT);
        mandateRepository.save(mandate);
    }

    private RedirectRequest creatRedirectRequest(Mandate mandateEntity) {
        final var mandateDto = mandateMapper.map(mandateEntity);
        return new RedirectRequest(mandateDto);
    }
} 