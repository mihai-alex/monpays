package com.monpays.dtos.audit;

import com.monpays.entities._generic.Operation;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AuditEntryDto {
    private String username;
    private Operation operation;
    private String uniqueEntityIdentifier;
    private Timestamp timestamp;
}
