package tech.me.direct.debit.service.mandate.complete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.me.direct.debit.config.properties.MandateProperties;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateRepository;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.Provider;
import tech.me.direct.debit.persistence.provider.ProviderRepository;
import tech.me.direct.debit.service.mandate.complete.mapper.CompleteMandateMapper;
import tech.me.direct.debit.service.mandate.exception.MandateNotFoundException;
import tech.me.direct.debit.service.mandate.exception.ProviderNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteMandateServiceImplTest {

    @Mock
    private MandateRepository mandateRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private MandateProperties mandateProperties;

    @Mock
    private CompleteMandateMapper completeMandateMapper;

    @InjectMocks
    private CompleteMandateServiceImpl completeMandateService;

    private CompleteMandateRequest request;
    private Mandate mandate;
    private Provider provider;

    @BeforeEach
    void setUp() {
        request = new CompleteMandateRequest(
                "mandate-ref-123",
                1,
                1000.0f,
                10,
                100.0f
        );
        mandate = new Mandate();
        provider = new Provider();
    }

    @Test
    void completeMandate_WhenAllDataValid_ShouldUpdateAndSaveMandate() {
        // Arrange
        when(mandateRepository.findByReferenceId(request.mandateReferenceId())).thenReturn(Optional.of(mandate));
        when(providerRepository.findById(request.providerId())).thenReturn(Optional.of(provider));
        when(mandateProperties.getDefaultExpiryDays()).thenReturn(30);

        // Act
        completeMandateService.completeMandate(request);

        // Assert
        verify(completeMandateMapper).updateMandate(
                eq(mandate),
                eq(provider),
                eq(request),
                eq(MandateStatus.DRAFT),
                eq(30)
        );
        verify(mandateRepository).save(mandate);
    }

    @Test
    void completeMandate_WhenMandateNotFound_ShouldThrowException() {
        // Arrange
        when(mandateRepository.findByReferenceId(request.mandateReferenceId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MandateNotFoundException.class,
                () -> completeMandateService.completeMandate(request),
                "Should throw MandateNotFoundException when mandate not found");

        verify(providerRepository, never()).findById(any());
        verify(completeMandateMapper, never()).updateMandate(any(), any(), any(), any(), any());
        verify(mandateRepository, never()).save(any());
    }

    @Test
    void completeMandate_WhenProviderNotFound_ShouldThrowException() {
        // Arrange
        when(mandateRepository.findByReferenceId(request.mandateReferenceId())).thenReturn(Optional.of(mandate));
        when(providerRepository.findById(request.providerId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class,
                () -> completeMandateService.completeMandate(request),
                "Should throw ProviderNotFoundException when provider not found");

        verify(completeMandateMapper, never()).updateMandate(any(), any(), any(), any(), any());
        verify(mandateRepository, never()).save(any());
    }
}