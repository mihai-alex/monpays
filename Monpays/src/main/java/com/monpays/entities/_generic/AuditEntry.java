package com.monpays.entities._generic;

import com.monpays.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Embedded
    private Operation operation;

    @Column
    private String uniqueEntityIdentifier;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;
}
