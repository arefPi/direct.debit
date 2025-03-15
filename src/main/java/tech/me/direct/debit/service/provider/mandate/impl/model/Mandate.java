package tech.me.direct.debit.service.provider.mandate.impl.model;

import java.time.LocalDateTime;

public record Mandate(
    String referenceId,
    float maxDailyTransferAmount,
    int maxDailyTransactionCount,
    float maxTransactionAmount,
    LocalDateTime expiresIn,
    Provider provider,
    User user) {}