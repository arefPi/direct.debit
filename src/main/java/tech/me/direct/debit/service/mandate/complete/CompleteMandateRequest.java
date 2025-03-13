package tech.me.direct.debit.service.mandate.complete;

public record CompleteMandateRequest(
        String mandateReferenceId,
        Integer providerId,
        Float maxDailyTransferAmount,
        Integer maxDailyTransactionCount,
        Float maxTransactionAmount
) {}
