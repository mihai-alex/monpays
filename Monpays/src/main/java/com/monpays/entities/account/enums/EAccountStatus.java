package com.monpays.entities.account.enums;

public enum EAccountStatus {
    CREATED, // waiting approval first time
    IN_REPAIR,
    REPAIRED, // waiting approval second time
    ACTIVE,
    MODIFIED, // waiting approval while active
    REMOVED,
}
