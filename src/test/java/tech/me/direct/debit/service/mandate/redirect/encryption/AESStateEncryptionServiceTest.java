package tech.me.direct.debit.service.mandate.redirect.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.me.direct.debit.config.properties.MandateAuthorizationCodeStateProperties;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectState;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AESStateEncryptionServiceTest {

    @Mock
    private MandateAuthorizationCodeStateProperties stateProperties;

    private AESStateEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        when(stateProperties.getSecret()).thenReturn("ThisIsA32ByteTestSecretKey12345!");
        encryptionService = new AESStateEncryptionService(objectMapper, stateProperties);
    }

    @Test
    void shouldEncryptAndDecryptSuccessfully() {
        // Given
        final var originalState = new RedirectState("user-123", "mandate-ref-456");

        // When
        final var encrypted = encryptionService.encrypt(originalState);
        final var decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertEquals(originalState.userId(), decrypted.userId());
        assertEquals(originalState.mandateReferenceId(), decrypted.mandateReferenceId());
    }

    @Test
    void shouldThrowExceptionOnInvalidEncryptedState() {
        // Given
        final var invalidEncryptedState = "InvalidBase64String";

        // When & Then
        assertThrows(IllegalStateException.class, () -> 
            encryptionService.decrypt(invalidEncryptedState)
        );
    }

    @Test
    void shouldGenerateDifferentCiphertextForSameState() {
        // Given
        final var state = new RedirectState("user-123", "mandate-ref-456");

        // When
        final var firstEncryption = encryptionService.encrypt(state);
        final var secondEncryption = encryptionService.encrypt(state);

        // Then
        assertNotEquals(firstEncryption, secondEncryption);
    }
}