package tech.me.direct.debit.persistence.mandate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.me.direct.debit.persistence.provider.Provider;
import tech.me.direct.debit.persistence.user.User;

@Entity
@Table(name = "mandates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mandate extends tech.me.direct.debit.persistence.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MandateStatus status;

    @Column(name = "max_daily_transfer_amount", nullable = false)
    private BigDecimal maxDailyTransferAmount;

    @Column(name = "max_daily_transaction_count", nullable = false)
    private Integer maxDailyTransactionCount;

    @Column(name = "max_transaction_amount", nullable = false)
    private BigDecimal maxTransactionAmount;

    @Column(name = "expires_in", nullable = false)
    private LocalDateTime expiresIn;

    @Column(name = "token", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Token token;

    @Column(name = "mandate_end_date", nullable = false)
    private LocalDateTime mandateEndDate;

    @ManyToOne
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
