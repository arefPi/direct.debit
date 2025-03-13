package tech.me.direct.debit.service.mandate.redirect.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tech.me.direct.debit.config.properties.MandateCallbackProperties;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.Provider;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.persistence.user.User;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.MandateNotInExpectedStatusException;
import tech.me.direct.debit.service.mandate.redirect.encryption.AESStateEncryptionService;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;
import tech.me.direct.debit.service.mandate.submit.SubmitMandateToMellatBankService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectToMellatBankServiceTest {

    private static final String MANDATE_REFERENCE_ID = "test-reference-id";
    private static final String USER_ID = "test-user-id";
    private static final String PROVIDER_CLIENT_ID = "test-client-id";
    private static final String PROVIDER_REDIRECT_URL = "https://test-redirect-url.com";
    private static final String ENCRYPTED_STATE = "encrypted-state-value";

    @Mock
    private MandateRepository mandateRepository;

    @Mock
    private AESStateEncryptionService aesStateEncryptionService;

    @Mock
    private SubmitMandateToMellatBankService submitMandateToMellatBankService;

    @Mock
    private MandateCallbackProperties mandateCallbackProperties;

    private RedirectToMellatBankService redirectToMellatBankService;

    private final User user = createUser();
    private final Provider provider = createProvider();
    private final Mandate mandate = createMandate();
    private final RedirectToProviderRequest request = new RedirectToProviderRequest(MANDATE_REFERENCE_ID);

    @BeforeEach 
    void setUp() {
        redirectToMellatBankService = new RedirectToMellatBankService(
                mandateRepository,
                aesStateEncryptionService,
                submitMandateToMellatBankService,
                mandateCallbackProperties
        );
    }

    private User createUser() {
        final var user = new User();
        user.setUserId(USER_ID);
        return user;
    }

    private Provider createProvider() {
        final var provider = new Provider();
        provider.setClientId(PROVIDER_CLIENT_ID);
        provider.setRedirectUrl(PROVIDER_REDIRECT_URL);
        return provider;
    }

    private Mandate createMandate() {
        final var mandate = new Mandate();
        mandate.setReferenceId(MANDATE_REFERENCE_ID);
        mandate.setStatus(MandateStatus.DRAFT);
        mandate.setUser(user);
        mandate.setProvider(provider);
        return mandate;
    }

    @Test
    void shouldReturnCorrectProviderId() {
        final var expectedProviderId = ProviderId.BKMTIRTH;
        
        final var actualProviderId = redirectToMellatBankService.providerId();
        
        assertEquals(expectedProviderId, actualProviderId);
    }

    @Test
    void shouldSuccessfullyRedirectToProvider() {
        // Given
        final var callbackUrl = "https://callback.test.com";
        when(mandateCallbackProperties.getUrl()).thenReturn(callbackUrl);
        final var expectedRedirectUri = "https://test-redirect-url.com?response_type=code&redirect_uri=" + callbackUrl + "&client_id=test-client-id&scope=mandate&state=encrypted-state-value&mandate_reference_id=test-reference-id";
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE_ID)).thenReturn(Optional.of(mandate));
        when(aesStateEncryptionService.encrypt(any())).thenReturn(ENCRYPTED_STATE);
        
        // When
        final var response = redirectToMellatBankService.redirect(request);
        
        // Then
        assertNotNull(response);
        assertEquals(expectedRedirectUri, response.redirectUrl());
        verify(submitMandateToMellatBankService).submit(MANDATE_REFERENCE_ID);
        verify(mandateRepository).save(mandateArgumentCaptor.capture());
        
        final var savedMandate = mandateArgumentCaptor.getValue();
        assertEquals(MandateStatus.SENT, savedMandate.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenMandateNotFound() {
        // Given
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE_ID)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(MandateNotFoundException.class, 
            () -> redirectToMellatBankService.redirect(request));
    }

    @Test
    void shouldThrowExceptionWhenMandateNotInDraftStatus() {
        // Given
        mandate.setStatus(MandateStatus.SENT);
        when(mandateRepository.findByReferenceId(MANDATE_REFERENCE_ID)).thenReturn(Optional.of(mandate));
        
        // When/Then
        assertThrows(MandateNotInExpectedStatusException.class, 
            () -> redirectToMellatBankService.redirect(request));
    }

    @Captor
    private ArgumentCaptor<Mandate> mandateArgumentCaptor;
}