package tech.me.direct.debit.service.mandate.create.reference.provider;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MandateReferenceIdProviderImpl implements MandateReferenceIdProvider {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
