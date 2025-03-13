package tech.me.direct.debit.service.mandate.redirect.encryption;

import tech.me.direct.debit.service.mandate.redirect.model.RedirectState;

public interface StateEncryptionService {
     String encrypt(RedirectState state);
     RedirectState decrypt(String encryptedState);
}
