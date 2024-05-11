package com.monpays.entities.payment.enums;

public enum EPaymentStatus {
    CREATED, // waiting approval first time
    IN_REPAIR,
    REPAIRED, // waiting approval second time
    WAITING_VERIFICATION,
    WAITING_AUTHORIZATION,
    CANCELLED,
    COMPLETED
}
