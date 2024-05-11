package com.monpays.mappers.profile;

import com.monpays.dtos.profile.ProfileHistoryEntryDto;
import com.monpays.dtos.profile.ProfilePendingResponseDto;
import com.monpays.dtos.profile.ProfileRequestDto;
import com.monpays.dtos.profile.ProfileResponseDto;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.profile.ProfileHistoryEntry;
import com.monpays.entities.profile.ProfilePending;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.type", target = "type")
    @Mapping(source = "dto.rights", target = "rights")
    @Mapping(source = "dto.version", target = "version")
    Profile fromRequestDto(ProfileRequestDto dto);


    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.version", target = "version")
    ProfileRequestDto toRequestDto(Profile entity);


    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.type", target = "type")
    @Mapping(source = "dto.rights", target = "rights")
    @Mapping(source = "dto.version", target = "version")
    Profile fromResponseDto(ProfileResponseDto dto);


    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.version", target = "version")
    ProfileResponseDto toResponseDto(Profile entity);


    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    ProfileHistoryEntryDto toHistoryEntryDto(ProfileHistoryEntry entity);


    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    ProfilePendingResponseDto toProfilePendingResponseDto(ProfilePending entity);


    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    Profile fromProfilePending(ProfilePending entity);


    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "entity", target = "originalProfile")
    ProfilePending toProfilePending(Profile entity, String username);

    default ProfilePending mapToProfilePending(Profile entity, String username) {
        ProfilePending profilePending = toProfilePending(entity, username);
        profilePending.setOriginalProfile(entity);
        return profilePending;
    }


    @Mapping(source = "entity.type", target = "type")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.rights", target = "rights")
    @Mapping(source = "entity.status", target = "status")
    ProfileHistoryEntry toProfileHistoryEntry(Profile entity);

    default ProfileHistoryEntry mapToProfileHistoryEntry(Profile entity) {
        ProfileHistoryEntry profileHistoryEntry = toProfileHistoryEntry(entity);
        profileHistoryEntry.setProfile(entity);
        return profileHistoryEntry;
    }
}
