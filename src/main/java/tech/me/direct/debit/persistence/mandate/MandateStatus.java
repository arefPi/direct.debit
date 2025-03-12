package tech.me.direct.debit.persistence.mandate;

public enum MandateStatus {
    INITIAL,
    DRAFT,
    SENT,
    WAITING_FOR_VERIFICATION,
    CANCELLED,
    FAILED,
    EXPIRED,
}
