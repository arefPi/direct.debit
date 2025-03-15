package tech.me.direct.debit.service.mandate.redirect.mapper;

import org.mapstruct.Mapper;
import tech.me.direct.debit.persistence.user.User;

@Mapper
public interface UserMapper {
    tech.me.direct.debit.service.provider.mandate.impl.model.User map(User user);
} 