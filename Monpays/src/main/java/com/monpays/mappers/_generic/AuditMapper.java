package com.monpays.mappers._generic;

import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities._generic.AuditEntry;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {
    public AuditEntryDto toAuditEntryDto(AuditEntry entity) {
        AuditEntryDto dto = new AuditEntryDto();
        dto.setUsername(entity.getUser().getUserName());
        dto.setOperation(entity.getOperation());
        dto.setUniqueEntityIdentifier(entity.getUniqueEntityIdentifier());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}
