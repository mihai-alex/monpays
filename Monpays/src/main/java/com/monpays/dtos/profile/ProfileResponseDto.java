package com.monpays.dtos.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities._generic.Operation;
import com.monpays.entities.profile.enums.EProfileStatus;
import com.monpays.entities.profile.enums.EProfileType;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDto {
    @Version
    private Long version;

    private String name;
    private EProfileType type;
    private List<Operation> rights;
    private EProfileStatus status; // added status to the response

    @Nullable
    private ProfilePendingResponseDto pendingEntity;

    @Nullable
    private List<ProfileHistoryEntryDto> history;

    @Nullable
    private List<AuditEntryDto> audit;
}
