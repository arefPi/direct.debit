package tech.me.direct.debit.service.mandate.redirect.model;

public record RedirectState(
        String userId,
        String mandateReferenceId) {
} 