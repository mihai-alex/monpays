package com.monpays.services.interfaces.profile;

import com.monpays.dtos.profile.ProfilePendingResponseDto;
import com.monpays.dtos.profile.ProfileRequestDto;
import com.monpays.dtos.profile.ProfileResponseDto;

import java.util.List;

public interface IProfileService {
    List<ProfileResponseDto> filterProfiles(String actorUsername, String columnName, String filterValue);
    List<ProfileResponseDto> getAll(String actorUsername);
    ProfileResponseDto getOne(String actorUsername, String name, boolean needsPending, boolean needsHistory, boolean needsAudit);
    ProfileResponseDto create(String actorUsername, ProfileRequestDto profileRequestDto);
    ProfilePendingResponseDto modify(String actorUsername, String name, ProfileRequestDto profileRequestDto);
    boolean remove(String actorUsername, String name);
    boolean approve(String actorUsername, String name);
    boolean reject(String actorUsername, String name);

    List<String> getProfileNames(String actorUsername);
}
