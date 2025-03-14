package tech.me.direct.debit.service.mandate.redirect.uri.builder;

import lombok.Builder;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.HashMap;
import java.util.Map;

@Builder
public class MandateAuthorizationCodeRequestUriBuilder implements AuthorizationCodeRequestUriBuilder {
    private final String providerAddress;
    private final String clientId;
    private final String redirectUri;
    private final String state;
    @Builder.Default
    private final Map<String, String> additionalParams = new HashMap<>();

    @Override
    public String toUriString() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(providerAddress)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("scope", "mandate")
                .queryParam("state", state);

        additionalParams.forEach(builder::queryParam);

        return builder
                .build()
                .encode()
                .toUriString();
    }
}
