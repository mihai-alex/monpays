package com.monpays.dtos.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities.user.enums.EUserStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    @Version
    private Long version;

    private String userName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String address;
    private String profileName; // the name of the profile
    private EUserStatus status;

    private boolean mfaEnabled = false;

    @Nullable
    private UserPendingResponseDto pendingEntity;

    @Nullable
    private List<UserHistoryEntryDto> history;

    @Nullable
    private List<AuditEntryDto> audit;
}
