package tech.me.direct.debit.util;

import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.provider.mandate.ProviderRedirectService;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class ProviderRedirectServiceRegistry implements
        ObjectRegistrant<ProviderId, ProviderRedirectService>,
        ObjectResolver<ProviderId, ProviderRedirectService> {
    private final Map<ProviderId, ProviderRedirectService> map;

    public ProviderRedirectServiceRegistry() {
        this.map = new EnumMap<>(ProviderId.class);
    }

    @Override
    public void register(ProviderId key, ProviderRedirectService value) {
        this.map.put(key, value);
    }

    @Override
    public Optional<ProviderRedirectService> resolve(ProviderId key) {
        return Optional.ofNullable(map.get(key));
    }
}
