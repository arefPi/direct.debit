package tech.me.direct.debit.service.provider.mandate;

import tech.me.direct.debit.service.provider.ProviderService;
import tech.me.direct.debit.service.provider.mandate.impl.model.redirect.RedirectRequest;
import tech.me.direct.debit.service.provider.mandate.impl.model.redirect.RedirectResponse;

public interface ProviderRedirectService extends ProviderService {
    RedirectResponse redirect(RedirectRequest request);
}
