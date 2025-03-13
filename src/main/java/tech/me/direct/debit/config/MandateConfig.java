package tech.me.direct.debit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mandate")
@Getter
@Setter
public class MandateConfig {
    private Integer defaultExpiryDays = 365;
} 