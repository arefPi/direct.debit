package tech.me.direct.debit.service.mandate.callback.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.mandate.Token;
import tech.me.direct.debit.service.mandate.callback.TokenMandateCallbackService;
import tech.me.direct.debit.service.mandate.exception.MandateCallbackInternalException;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenService;
import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenRequest;
import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenMandateCallbackServiceImpl implements TokenMandateCallbackService {
    private final MandateRepository mandateRepository;
    private final GetProviderAccessTokenService getProviderAccessTokenService;

    @Override
    public void handleTokenCallback(Mandate mandate, String code) {
        try {
            final var tokenResponse = getProviderAccessTokenResponse(mandate, code);
            updateMandateWithAccessToken(mandate, tokenResponse);
        } catch (Exception e) {
            throw new MandateCallbackInternalException("Failed to get provider access token", e);
        }
    }

    private GetProviderAccessTokenResponse getProviderAccessTokenResponse(Mandate mandate, String code) {
        final var provider = mandate.getProvider();
        final var request = new GetProviderAccessTokenRequest(
            code,
            provider.getClientId(),
            provider.getClientSecret()
        );
        return getProviderAccessTokenService.getAccessToken(request);
    }

    private void updateMandateWithAccessToken(Mandate mandate, GetProviderAccessTokenResponse getProviderAccessTokenResponse) {
        final var accessToken = getProviderAccessTokenResponse.accessToken();
        final var refreshToken = getProviderAccessTokenResponse.refreshToken();
        final var scope = getProviderAccessTokenResponse.scope();
        final var expiresIn = getProviderAccessTokenResponse.expiresIn();

        final var token = new Token(accessToken, refreshToken, scope, expiresIn);
        mandate.setToken(token);

        mandate.setStatus(MandateStatus.ACTIVE);
        mandateRepository.save(mandate);
    }
} 