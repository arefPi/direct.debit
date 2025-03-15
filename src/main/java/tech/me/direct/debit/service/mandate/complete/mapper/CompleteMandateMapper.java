package tech.me.direct.debit.service.mandate.complete.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.mandate.MandateStatus;
import tech.me.direct.debit.persistence.provider.Provider;
import tech.me.direct.debit.service.mandate.complete.CompleteMandateRequest;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CompleteMandateMapper {

    @Mapping(target = "provider", source = "provider")
    @Mapping(target = "expiresIn", expression = "java(calculateExpiryDate(expiryDays))")
    void updateMandate(@MappingTarget Mandate mandate, 
                      Provider provider,
                      CompleteMandateRequest request,
                      MandateStatus status,
                      Integer expiryDays);

    default LocalDateTime calculateExpiryDate(Integer expiryDays) {
        return LocalDateTime.now().plusDays(expiryDays);
    }
} 