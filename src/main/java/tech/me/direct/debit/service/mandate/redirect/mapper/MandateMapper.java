package tech.me.direct.debit.service.mandate.redirect.mapper;

import org.mapstruct.Mapper;
import tech.me.direct.debit.service.provider.mandate.impl.model.Mandate;

@Mapper(componentModel = "spring", uses = {ProviderMapper.class, UserMapper.class})
public interface MandateMapper {
    Mandate map(tech.me.direct.debit.persistence.mandate.Mandate mandate);
} 