package tech.me.direct.debit.service.mandate.get.access.token.impl;

import org.springframework.stereotype.Component;
import tech.me.direct.debit.service.mandate.get.access.token.GetProviderAccessTokenService;
import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenRequest;
import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenResponse;

@Component
public class GetProviderAccessTokenServiceImpl implements GetProviderAccessTokenService {
    @Override
    public GetProviderAccessTokenResponse getAccessToken(GetProviderAccessTokenRequest request) {
        return null;
    }
}
