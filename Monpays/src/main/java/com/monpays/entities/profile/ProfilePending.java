package com.monpays.entities.profile;

import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "pending_profiles")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePending {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private EProfileType type;

    @OneToOne
    @JoinColumn(name = "original_profile_name", referencedColumnName = "name", unique = true)
    private Profile originalProfile;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Operation> rights;

    @Column
    @Enumerated(EnumType.STRING)
    private EProfileStatus status;

    @Column
    private String username; // the username of the user who created the pending entry
}
