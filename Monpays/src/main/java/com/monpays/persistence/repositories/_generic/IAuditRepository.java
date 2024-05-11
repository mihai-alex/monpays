package com.monpays.persistence.repositories._generic;

import com.monpays.entities._generic.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAuditRepository extends JpaRepository<AuditEntry, Long> {
    @Query("SELECT a FROM AuditEntry a WHERE a.operation.groupName = ?1")
    List<AuditEntry> findAllBygroupName(String groupName);
    @Query("SELECT a FROM AuditEntry a WHERE a.operation.groupName = ?1 AND a.uniqueEntityIdentifier = ?2")
    List<AuditEntry> findAllBygroupNameAndObjectId(String groupName, String uniqueEntityIdentifier);
}
