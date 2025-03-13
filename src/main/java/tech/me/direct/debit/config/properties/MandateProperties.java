package tech.me.direct.debit.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "mandate")
@Configuration
@Getter
@Setter
public class MandateProperties {
    private Integer defaultExpiryDays = 365;
}