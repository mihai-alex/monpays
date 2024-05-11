package com.monpays.entities._generic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractHistoryEntry {
    @Temporal(TemporalType.TIMESTAMP)
    protected Timestamp historyEntryCreationTimestamp;
}
