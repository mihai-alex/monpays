package com.monpays.dtos.profile;

import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.List;

@Data
public class ProfileRequestDto {
    @Version
    private Long version;

    private String name;
    private EProfileType type;
    private List<Operation> rights;
}
