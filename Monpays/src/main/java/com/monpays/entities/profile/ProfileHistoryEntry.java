package com.monpays.entities.profile;

import com.monpays.entities._generic.AbstractHistoryEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "profile_history")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileHistoryEntry extends AbstractHistoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @Column
    @Enumerated(EnumType.STRING)
    protected EProfileType type;
    @Column
    protected String name;
    @ElementCollection(fetch = FetchType.EAGER)
    protected List<Operation> rights;
    @Column
    @Enumerated(EnumType.STRING)
    protected EProfileStatus status;
    @ManyToOne
    private Profile profile;

    public ProfileHistoryEntry(Profile profile, Timestamp timestamp) {
        super(timestamp);
        this.id = 0L;
        this.type = profile.getType();
        this.name = profile.getName();
        this.rights = profile.getRights();
        this.status = profile.getStatus();
        this.profile = profile;
    }
}
