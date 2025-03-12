package tech.me.direct.debit.persistence.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import tech.me.direct.debit.persistence.mandate.Mandate;
import tech.me.direct.debit.persistence.user.User;

@Entity
@Table(name = "payments")
public class Payment extends tech.me.direct.debit.persistence.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "transaction_reference", nullable = false, unique = true)
    private String transactionReference;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "mandate_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Mandate mandate;
}
