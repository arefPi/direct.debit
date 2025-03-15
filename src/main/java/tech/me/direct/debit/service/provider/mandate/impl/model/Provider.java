package tech.me.direct.debit.service.provider.mandate.impl.model;

import tech.me.direct.debit.persistence.provider.ProviderId;

public record Provider(
    ProviderId providerId,
    String redirectUrl,
    String clientId) {}