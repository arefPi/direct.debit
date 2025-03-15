package tech.me.direct.debit.service.provider.mandate.impl.mellat.redirect;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.config.properties.MandateCallbackProperties;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.provider.mandate.ProviderRedirectService;
import tech.me.direct.debit.service.provider.mandate.ProviderSubmitMandateService;
import tech.me.direct.debit.service.provider.mandate.impl.mellat.redirect.encryption.AESStateEncryptionService;
import tech.me.direct.debit.service.provider.mandate.impl.mellat.redirect.encryption.RedirectState;
import tech.me.direct.debit.service.provider.mandate.impl.mellat.redirect.url.builder.MandateAuthorizationCodeRequestUriBuilder;
import tech.me.direct.debit.service.provider.mandate.impl.model.Mandate;
import tech.me.direct.debit.service.provider.mandate.impl.model.SubmitMandateRequest;
import tech.me.direct.debit.service.provider.mandate.impl.model.redirect.RedirectRequest;
import tech.me.direct.debit.service.provider.mandate.impl.model.redirect.RedirectResponse;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class MellatBankRedirectService implements ProviderRedirectService {
    private final AESStateEncryptionService aesStateEncryptionService;
    private final MandateCallbackProperties mandateCallbackProperties;
    private final ProviderSubmitMandateService mellatSubmitMandateService;

    @Override
    public RedirectResponse redirect(RedirectRequest request) {
        final var mandate = request.mandate();
        final var submitMandateRequest = new SubmitMandateRequest(mandate);

        mellatSubmitMandateService.submit(submitMandateRequest);

        return createRedirectToProviderResponse(mandate);
    }

    private RedirectResponse createRedirectToProviderResponse(Mandate mandate) {
        final var userId = mandate.user().userId();
        final var referenceId = mandate.referenceId();

        final var state = new RedirectState(userId, referenceId);
        final var encryptedState = aesStateEncryptionService.encrypt(state);


        final var providerAddress = mandate.provider().redirectUrl();
        final var clientId = mandate.provider().clientId();
        final var redirectUri = mandateCallbackProperties.getUrl();

        final var url = MandateAuthorizationCodeRequestUriBuilder.builder()
                .providerAddress(providerAddress)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .state(encryptedState)
                .additionalParams(Map.of("mandate_reference_id", referenceId))
                .build()
                .toUriString();

        return new RedirectResponse(url);
    }

    @Override
    public ProviderId providerId() {
        return ProviderId.BKMTIRTH;
    }
}
