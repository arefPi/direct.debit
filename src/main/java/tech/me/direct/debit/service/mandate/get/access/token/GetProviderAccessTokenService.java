package tech.me.direct.debit.service.mandate.get.access.token;

import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenRequest;
import tech.me.direct.debit.service.mandate.get.access.token.model.GetProviderAccessTokenResponse;

public interface GetProviderAccessTokenService {
    GetProviderAccessTokenResponse getAccessToken(GetProviderAccessTokenRequest request);
} 