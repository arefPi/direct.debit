package tech.me.direct.debit.service.mandate.redirect;

import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderRequest;
import tech.me.direct.debit.service.mandate.redirect.model.RedirectToProviderResponse;

public interface RedirectToProviderService {
    RedirectToProviderResponse redirect(RedirectToProviderRequest request);
}
