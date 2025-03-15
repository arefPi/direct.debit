package tech.me.direct.debit.service.mandate.get.access.token;

public record GetProviderAccessTokenRequest(
    String code,
    String clientId,
    String clientSecret
) {
    public static GetProviderAccessTokenRequest of(String code, String clientId, String clientSecret) {
        return new GetProviderAccessTokenRequest(code, clientId, clientSecret);
    }
} 