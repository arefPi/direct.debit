package tech.me.direct.debit.service.mandate.callback.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.service.mandate.callback.MandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.InvalidStateException;
import tech.me.direct.debit.service.mandate.exception.MandateCallbackInternalException;
import tech.me.direct.debit.util.encryption.AESStateEncryptionService;
import tech.me.direct.debit.util.encryption.RedirectState;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenService;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenRequest;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenResponse;
import tech.me.direct.debit.persistence.mandate.Token;

@Service
@RequiredArgsConstructor
public class MandateCallbackServiceImpl implements MandateCallbackService {
    private final MandateRepository mandateRepository;
    private final AESStateEncryptionService aesStateEncryptionService;
    private final GetProviderAccessTokenService getProviderAccessTokenService;

    @Override
    public void handleCallback(MandateCallbackRequest request) {
        final var state = decryptState(request.state());
        final var mandate = validateAndGetMandate(state);

        if (request.error() != null) {
            handleError(mandate, request.error());
        }

        updateMandateToWaitingForVerification(mandate);

        try {
            final var tokenResponse = getProviderAccessTokenResponse(mandate, request.code());
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

    private RedirectState decryptState(String encryptedState) {
        return aesStateEncryptionService.decrypt(encryptedState);
    }

    private Mandate validateAndGetMandate(RedirectState state) {
        final var mandate = mandateRepository.findByReferenceId(state.mandateReferenceId())
                .orElseThrow(() -> new MandateNotFoundException(state.mandateReferenceId()));

        if (!state.userId().equals(mandate.getUser().getUserId())) {
            throw new InvalidStateException(mandate.getReferenceId());
        }

        return mandate;
    }

    private void updateMandateToWaitingForVerification(Mandate mandate) {
        mandate.setStatus(MandateStatus.WAITING_FOR_VERIFICATION);
        mandateRepository.save(mandate);
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

    private void handleError(Mandate mandate, String error) {
        switch (error) {
            case "canceled" -> {
                mandate.setStatus(MandateStatus.CANCELLED);
                mandateRepository.save(mandate);
                throw new MandateCallbackInternalException(error);
            }
            case "failed" -> {
                mandate.setStatus(MandateStatus.FAILED);
                mandateRepository.save(mandate);
                throw new MandateCallbackInternalException(error);
            }
            default -> throw new MandateCallbackInternalException(error);
        }
    }
} 