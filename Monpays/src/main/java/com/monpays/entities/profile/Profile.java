package com.monpays.entities.profile;

import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "profiles")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Version
    private Long version;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private EProfileType type;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Operation> rights;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private EProfileStatus status;
}
