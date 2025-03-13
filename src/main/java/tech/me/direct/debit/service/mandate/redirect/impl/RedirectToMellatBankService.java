package tech.me.direct.debit.service.mandate.redirect.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tech.me.direct.debit.config.properties.MandateCallbackProperties;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.MandateNotInExpectedStatusException;
import tech.me.direct.debit.service.mandate.redirect.RedirectToProviderService;
import tech.me.direct.debit.service.mandate.redirect.encryption.AESStateEncryptionService;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectState;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderResponse;
import tech.me.direct.debit.service.mandate.redirect.uri.builder.MandateAuthorizationCodeRequestUriBuilder;
import tech.me.direct.debit.service.mandate.submit.SubmitMandateToMellatBankService;

@RequiredArgsConstructor
@Service
public class RedirectToMellatBankService implements RedirectToProviderService {
    private final MandateRepository mandateRepository;
    private final AESStateEncryptionService aesStateEncryptionService;
    private final SubmitMandateToMellatBankService submitMandateToMellatBankService;
    private final MandateCallbackProperties mandateCallbackProperties;

    @Override
    public RedirectToProviderResponse redirect(RedirectToProviderRequest request) {
        final var mandateReferenceId = request.mandateReferenceId();

        final var mandate = getMandate(mandateReferenceId);

        validateMandateStatus(mandate);

        submitMandateToMellatBankService.submit(mandateReferenceId);

        final var redirectToProviderResponse =
                createRedirectToProviderResponse(mandate);

        changeMandateStatusToSent(mandate);

        return redirectToProviderResponse;
    }

    private Mandate getMandate(String mandateReferenceId) {
        return mandateRepository.findByReferenceId(mandateReferenceId)
                .orElseThrow(() -> new MandateNotFoundException(mandateReferenceId));
    }

    private void validateMandateStatus(Mandate mandate) {
        if (!MandateStatus.DRAFT.equals(mandate.getStatus())) {
            final var status = mandate.getStatus()
                    != null ? mandate.getStatus().name() : null;
            throw new MandateNotInExpectedStatusException(status);
        }
    }

    private void changeMandateStatusToSent(Mandate mandate) {
        mandate.setStatus(MandateStatus.SENT);
        mandateRepository.save(mandate);
    }

    private RedirectToProviderResponse createRedirectToProviderResponse(Mandate mandate) {
        final var userId = mandate.getUser().getUserId();
        final var referenceId = mandate.getReferenceId();

        final var state = new RedirectState(userId, referenceId);
        final var encryptedState = aesStateEncryptionService.encrypt(state);

        final var provider = mandate.getProvider();
        final var providerAddress = provider.getRedirectUrl();
        final var clientId = provider.getClientId();
        final var redirectUri = mandateCallbackProperties.getUrl();

        final var url = MandateAuthorizationCodeRequestUriBuilder.builder()
                .providerAddress(providerAddress)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .state(encryptedState)
                .mandateReferenceId(referenceId)
                .build()
                .toUriString();

        return new RedirectToProviderResponse(url);
    }

    @Override
    public ProviderId providerId() {
        return ProviderId.BKMTIRTH;
    }
}
