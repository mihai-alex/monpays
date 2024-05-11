package com.monpays.dtos.profile;

import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import lombok.Data;

import java.util.List;

@Data
public class ProfileHistoryEntryDto {
    private EProfileType type;
    private String name;
    private List<Operation> rights;
    private EProfileStatus status;
}
