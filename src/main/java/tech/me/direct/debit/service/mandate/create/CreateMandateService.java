package tech.me.direct.debit.service.mandate.create;

import tech.me.direct.debit.service.mandate.create.model.CreateMandateRequest;
import tech.me.direct.debit.service.mandate.create.model.CreateMandateResponse;

public interface CreateMandateService {
    CreateMandateResponse create(CreateMandateRequest request);
}
