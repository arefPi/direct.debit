package tech.me.direct.debit.util.encryption;

public record RedirectState(
        String userId,
        String mandateReferenceId) {
} 