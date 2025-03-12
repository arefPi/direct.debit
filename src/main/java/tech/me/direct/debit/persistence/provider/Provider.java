package tech.me.direct.debit.persistence.provider;

import jakarta.persistence.*;

@Entity
@Table(name = "providers")
public class Provider extends tech.me.direct.debit.persistence.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;

    @Column(name = "redirect_url", nullable = false)
    private String redirectUrl;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false)
    private String clientSecret;
}
