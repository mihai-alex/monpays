package com.monpays.entities.user.enums;

public enum EUserStatus {
    CREATED, // waiting approval first time
    IN_REPAIR,
    REPAIRED, // waiting approval second time
    ACTIVE,
    MODIFIED, // waiting approval while active
    BLOCKED,
    REMOVED,
}
