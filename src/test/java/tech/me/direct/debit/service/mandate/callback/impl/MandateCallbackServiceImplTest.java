package tech.me.direct.debit.service.mandate.callback.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.service.mandate.callback.InitialMandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.TokenMandateCallbackService;
import tech.me.direct.debit.service.mandate.callback.model.MandateCallbackRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MandateCallbackServiceImplTest {
    private static final String AUTH_CODE = "auth-code";
    private static final String ENCRYPTED_STATE = "encrypted-state";

    @Mock
    private InitialMandateCallbackService initialMandateCallbackService;

    @Mock
    private TokenMandateCallbackService tokenMandateCallbackService;

    @InjectMocks
    private MandateCallbackServiceImpl mandateCallbackService;

    private MandateCallbackRequest request;
    private Mandate mandate;

    @BeforeEach
    void setUp() {
        request = new MandateCallbackRequest(AUTH_CODE, ENCRYPTED_STATE, null);
        mandate = new Mandate();
    }

    @Test
    void handleCallback_ShouldProcessInitialAndTokenCallbacks() {
        // Given
        when(initialMandateCallbackService.handleInitialCallback(request)).thenReturn(mandate);

        // When
        mandateCallbackService.handleCallback(request);

        // Then
        verify(initialMandateCallbackService).handleInitialCallback(request);
        verify(tokenMandateCallbackService).handleTokenCallback(mandate, AUTH_CODE);
    }
} 