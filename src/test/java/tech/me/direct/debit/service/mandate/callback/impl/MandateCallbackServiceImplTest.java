package tech.me.direct.debit.service.mandate.callback.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.Provider;
import tech.me.direct.debit.persistence.user.User;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;
import tech.me.direct.debit.service.mandate.exception.InvalidStateException;
import tech.me.direct.debit.service.mandate.exception.MandateCallbackInternalException;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenService;
import tech.me.direct.debit.util.encryption.AESStateEncryptionService;
import tech.me.direct.debit.util.encryption.RedirectState;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MandateCallbackServiceImplTest {
    private static final String USER_ID = "test-user-id";
    private static final String MANDATE_REFERENCE = "test-mandate-reference";
    private static final String ENCRYPTED_STATE = "encrypted-state";
    private static final String AUTH_CODE = "auth-code";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";

    @Mock
    private MandateRepository mandateRepository;

    @Mock
    private AESStateEncryptionService aesStateEncryptionService;

    @Mock
    private GetProviderAccessTokenService getProviderAccessTokenService;

    @InjectMocks
    private MandateCallbackServiceImpl mandateCallbackService;

    @Captor
    private ArgumentCaptor<Mandate> mandateCaptor;

    private Mandate mandate;
    private User user;
    private RedirectState state;
    private MandateCallbackRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);

        Provider provider = new Provider();
        provider.setClientId(CLIENT_ID);
        provider.setClientSecret(CLIENT_SECRET);

        mandate = new Mandate();
        mandate.setReferenceId(MANDATE_REFERENCE);
        mandate.setUser(user);
        mandate.setProvider(provider);
        mandate.setStatus(MandateStatus.SENT);

        state = new RedirectState(USER_ID, MANDATE_REFERENCE);
        request = new MandateCallbackRequest(AUTH_CODE, ENCRYPTED_STATE, null);
    }

    @Test
    void handleCallback_WhenMandateNotFound_ShouldThrowException() {
        // Given
        when(aesStateEncryptionService.decrypt(ENCRYPTED_STATE)).thenReturn(state);
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(MandateNotFoundException.class,
            () -> mandateCallbackService.handleCallback(request)
        );

        verify(getProviderAccessTokenService, never()).getAccessToken(any());
        verify(mandateRepository, never()).save(any());
    }

    @Test
    void handleCallback_WhenUserIdMismatch_ShouldThrowException() {
        // Given
        user.setUserId("different-user-id");
        when(aesStateEncryptionService.decrypt(ENCRYPTED_STATE)).thenReturn(state);
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE)).thenReturn(Optional.of(mandate));

        // When/Then
        assertThrows(InvalidStateException.class,
            () -> mandateCallbackService.handleCallback(request)
        );

        verify(getProviderAccessTokenService, never()).getAccessToken(any());
        verify(mandateRepository, never()).save(any());
    }

    @Test
    void handleCallback_WhenErrorInRequest_ShouldThrowException() {
        // Given
        var errorRequest = new MandateCallbackRequest(null, ENCRYPTED_STATE, "failed");
        when(aesStateEncryptionService.decrypt(ENCRYPTED_STATE)).thenReturn(state);
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE)).thenReturn(Optional.of(mandate));

        // When/Then
        var exception = assertThrows(MandateCallbackInternalException.class,
            () -> mandateCallbackService.handleCallback(errorRequest)
        );

        assertThat(exception.getError()).isEqualTo("failed");
        verify(getProviderAccessTokenService, never()).getAccessToken(any());
        verify(mandateRepository).save(mandateCaptor.capture());
        assertThat(mandateCaptor.getValue().getStatus()).isEqualTo(MandateStatus.FAILED);
    }

    @Test
    void handleCallback_WhenTokenRequestFails_ShouldThrowException() {
        // Given
        when(aesStateEncryptionService.decrypt(ENCRYPTED_STATE)).thenReturn(state);
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE)).thenReturn(Optional.of(mandate));
        when(getProviderAccessTokenService.getAccessToken(any())).thenThrow(new RuntimeException("Token request failed"));

        // When/Then
        var exception = assertThrows(MandateCallbackInternalException.class,
            () -> mandateCallbackService.handleCallback(request)
        );

        assertThat(exception.getError()).isEqualTo("Failed to get provider access token");
        verify(mandateRepository, times(1)).save(any()); // Only for WAITING_FOR_VERIFICATION status
    }
} 