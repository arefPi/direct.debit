package tech.me.direct.debit.util.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.me.direct.debit.config.properties.MandateAuthorizationCodeStateProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AESStateEncryptionService implements StateEncryptionService {
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private final ObjectMapper objectMapper;
    private final MandateAuthorizationCodeStateProperties stateProperties;

    public String encrypt(RedirectState state) {
        try {
            final var json = objectMapper.writeValueAsString(state);
            final var secretKey = new SecretKeySpec(stateProperties.getSecret().getBytes(StandardCharsets.UTF_8), "AES");

            final var nonce = new byte[GCM_NONCE_LENGTH];
            new SecureRandom().nextBytes(nonce);
            final var gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            
            final var cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            final var encrypted = cipher.doFinal(json.getBytes());

            final var combined = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(encrypted, 0, combined, nonce.length, encrypted.length);
            
            return Base64.getUrlEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt state", e);
        }
    }

    public RedirectState decrypt(String encryptedState) {
        try {
            final var combined = Base64.getUrlDecoder().decode(encryptedState);

            final var nonce = new byte[GCM_NONCE_LENGTH];
            final var encrypted = new byte[combined.length - GCM_NONCE_LENGTH];
            System.arraycopy(combined, 0, nonce, 0, GCM_NONCE_LENGTH);
            System.arraycopy(combined, GCM_NONCE_LENGTH, encrypted, 0, encrypted.length);
            
            final var secretKey = new SecretKeySpec(stateProperties.getSecret().getBytes(StandardCharsets.UTF_8), "AES");
            final var gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            
            final var cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            final var decrypted = cipher.doFinal(encrypted);
            return objectMapper.readValue(decrypted, RedirectState.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt state", e);
        }
    }
} 