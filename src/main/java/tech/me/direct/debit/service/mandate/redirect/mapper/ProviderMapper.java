package tech.me.direct.debit.service.mandate.redirect.mapper;

import org.mapstruct.Mapper;
import tech.me.direct.debit.service.provider.mandate.impl.model.Provider;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    Provider map(tech.me.direct.debit.persistence.provider.Provider provider);
} 