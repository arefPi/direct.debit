package tech.me.direct.debit.config.properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.me.direct.debit.persistence.provider.ProviderId;
import tech.me.direct.debit.service.provider.mandate.ProviderRedirectService;
import tech.me.direct.debit.util.ObjectResolver;
import tech.me.direct.debit.util.ProviderRedirectServiceRegistry;

import java.util.List;

@Configuration
public class BeanConfig {
    @Bean
    ObjectResolver<ProviderId, ProviderRedirectService> providerRedirectServiceResolver(
            List<ProviderRedirectService> providerRedirectServices) {
        final var providerRedirectServiceRegistry = new ProviderRedirectServiceRegistry();

        providerRedirectServices.forEach(providerRedirectService ->
                providerRedirectServiceRegistry.register(providerRedirectService.providerId(),
                        providerRedirectService));

        return providerRedirectServiceRegistry;
    }
}
