package tech.me.direct.debit.controller.mandate.complete;

public record CompleteMandateRequest(
        String mandateReferenceId,
        Integer providerId,
        Float maxDailyTransferAmount,
        Integer maxDailyTransactionCount,
        Float maxTransactionAmount
) {} 