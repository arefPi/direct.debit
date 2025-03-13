package tech.me.direct.debit.service.mandate.redirect.uri.builder;

import lombok.Builder;
import org.springframework.web.util.UriComponentsBuilder;
@Builder
public class MandateAuthorizationCodeRequestUriBuilder implements AuthorizationCodeRequestUriBuilder {
    private final String providerAddress;
    private final String clientId;
    private final String redirectUri;
    private final String state;
    private final String mandateReferenceId;

    @Override
    public String toUriString() {
        return UriComponentsBuilder
                .fromUriString(providerAddress)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("scope", "mandate")
                .queryParam("state", state)
                .queryParam("mandate_reference_id", mandateReferenceId)
                .build()
                .encode()
                .toUriString();
    }
}
