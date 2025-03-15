package tech.me.direct.debit.service.provider.mandate.impl.model;

public record User(
    String userId,
    String nationalId,
    String phoneNumber) {}