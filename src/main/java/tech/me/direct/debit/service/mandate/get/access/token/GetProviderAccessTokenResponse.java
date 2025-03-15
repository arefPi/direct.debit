package tech.me.direct.debit.service.mandate.get.access.token;

public record GetProviderAccessTokenResponse(
    String accessToken,
    String tokenType,
    int expiresIn,
    String refreshToken,
    String scope
) {
} 