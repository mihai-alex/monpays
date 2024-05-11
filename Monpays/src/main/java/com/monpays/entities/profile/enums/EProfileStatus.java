package com.monpays.entities.profile.enums;

public enum EProfileStatus {
    CREATED, // waiting approval first time
    IN_REPAIR,
    REPAIRED, // waiting approval second time
    ACTIVE,
    MODIFIED, // waiting approval while active
    REMOVED,
}
