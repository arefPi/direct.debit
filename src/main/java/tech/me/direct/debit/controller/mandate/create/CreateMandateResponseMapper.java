package tech.me.direct.debit.controller.mandate.create;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateMandateResponseMapper {
    CreateMandateResponse map(tech.me.direct.debit.service.mandate.create.model.CreateMandateResponse source);
}
