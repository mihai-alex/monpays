package com.monpays.services.interfaces._generic;

import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAuditService {
    AuditEntry add(User user, Operation operation, String uniqueEntityIdentifier);
    AuditEntry add(AuditEntry auditEntry);
    List<AuditEntry> getAll();
    List<AuditEntry> getByClassProfile(String groupName);
    List<AuditEntry> getByObject(String groupName, String uniqueEntityIdentifier);
}
