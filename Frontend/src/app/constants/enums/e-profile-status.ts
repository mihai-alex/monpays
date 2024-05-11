export enum EProfileStatus {
    CREATED = 'CREATED', // waiting approval first time
    IN_REPAIR = 'IN_REPAIR',
    REPAIRED = 'REPAIRED', // waiting approval second time
    ACTIVE = 'ACTIVE',
    MODIFIED = 'MODIFIED', // waiting approval while active
    REMOVED = 'REMOVED',
}
      