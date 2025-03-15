package tech.me.direct.debit.service.provider.mandate.impl.mellat.redirect.encryption;

public interface StateEncryptionService {
     String encrypt(RedirectState state);
     RedirectState decrypt(String encryptedState);
}
