package tech.me.direct.debit.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("mandate.authorization-code.state")
@Configuration
@Getter
@Setter
public class MandateAuthorizationCodeStateProperties {
    private String secret;
} 