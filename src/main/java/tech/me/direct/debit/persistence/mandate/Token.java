package tech.me.direct.debit.persistence.mandate;

public record Token(String accessToken,
                    String refreshToken,
                    String scope,
                    int expiresIn) {
}
