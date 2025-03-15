package tech.me.direct.debit.service.mandate.callback.model;

public record MandateCallbackRequest(
        String code,
        String state,
        String error
) {} 