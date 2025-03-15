package tech.me.direct.debit.util.encryption;

public interface StateEncryptionService {
     String encrypt(RedirectState state);
     RedirectState decrypt(String encryptedState);
}
