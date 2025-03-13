package tech.me.direct.debit.controller.mandate.complete;

import org.mapstruct.Mapper;

@Mapper
public interface CompleteMandateRequestMapper {
    tech.me.direct.debit.service.mandate.complete.CompleteMandateRequest map(CompleteMandateRequest source);
} 