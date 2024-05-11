package com.monpays.mappers.user;

import com.monpays.dtos.user.*;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.User;
import com.monpays.entities.user.UserHistoryEntry;
import com.monpays.entities.user.UserPending;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "isFirstLogin", expression = "java(true)")
    @Mapping(source = "dto.userName", target = "userName")
    @Mapping(source = "dto.firstName", target = "firstName")
    @Mapping(source = "dto.lastName", target = "lastName")
    @Mapping(source = "dto.emailAddress", target = "emailAddress")
    @Mapping(source = "dto.address", target = "address")
    @Mapping(source = "dto.phoneNumber", target = "phoneNumber")
    @Mapping(target = "profile", expression = "java(mapProfileName2Profile(dto.getProfileName(), profileRepository))")
    @Mapping(source = "dto.version", target = "version")
    User fromRequestDto(UserRequestDto dto, @Context IProfileRepository profileRepository);


    @Mapping(source = "dto.userName", target = "userName")
    @Mapping(source = "dto.firstName", target = "firstName")
    @Mapping(source = "dto.lastName", target = "lastName")
    @Mapping(source = "dto.emailAddress", target = "emailAddress")
    @Mapping(source = "dto.address", target = "address")
    @Mapping(source = "dto.phoneNumber", target = "phoneNumber")
    @Mapping(source = "dto.profileName", target = "profileName")
    @Mapping(target = "version", ignore = true)
    UserRequestDto toUserRequestDto(UserSignUpDto dto);

    @Mapping(source = "entity.userName", target = "userName")
    @Mapping(source = "entity.firstName", target = "firstName")
    @Mapping(source = "entity.lastName", target = "lastName")
    @Mapping(source = "entity.emailAddress", target = "emailAddress")
    @Mapping(source = "entity.address", target = "address")
    @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "entity.profile.name", target = "profileName")
    @Mapping(source = "entity.version", target = "version")
    UserResponseDto toResponseDto(User entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isFirstLogin", expression = "java(true)")
    @Mapping(target = "userName", source = "dto.userName")
    @Mapping(target = "password", source = "hashedPassword")
    @Mapping(target = "firstName", source = "dto.firstName")
    @Mapping(target = "lastName", source = "dto.lastName")
    @Mapping(target = "emailAddress", source = "dto.emailAddress")
    @Mapping(target = "address", source = "dto.address")
    @Mapping(target = "phoneNumber", source = "dto.phoneNumber")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "mfaEnabled", source = "dto.mfaEnabled")
    User fromUserSignUpDto(UserSignUpDto dto, String hashedPassword, Profile profile);

    @Mapping(source = "entity.userName", target = "userName")
    @Mapping(source = "entity.firstName", target = "firstName")
    @Mapping(source = "entity.lastName", target = "lastName")
    @Mapping(source = "entity.emailAddress", target = "emailAddress")
    @Mapping(source = "entity.address", target = "address")
    @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "entity.profile.name", target = "profileName")
    UserHistoryEntryDto toHistoryEntryDto(UserHistoryEntry entity);

    default Profile mapProfileName2Profile(String profileName, @Context IProfileRepository profileRepository) {
        return profileRepository.findByName(profileName).orElseThrow();
    }

    @Mapping(target = "originalUser", expression = "java(entity)")
    @Mapping(source = "entity.firstName", target = "firstName")
    @Mapping(source = "entity.lastName", target = "lastName")
    @Mapping(source = "entity.emailAddress", target = "emailAddress")
    @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "entity.address", target = "address")
    @Mapping(source = "entity.profile.name", target = "profileName")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "username", target = "actorUserName")
    UserPending toUserPending(User entity, String username);

    default UserPending mapToUserPending(User entity, String username) {
        UserPending userPending = toUserPending(entity, username);
        userPending.setOriginalUser(entity);
        return userPending;
    }

    @Mapping(source = "entity.originalUser.userName", target = "userName")
    @Mapping(source = "entity.firstName", target = "firstName")
    @Mapping(source = "entity.lastName", target = "lastName")
    @Mapping(source = "entity.emailAddress", target = "emailAddress")
    @Mapping(source = "entity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "entity.address", target = "address")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.profileName", target = "profileName")
    UserPendingResponseDto toUserPendingResponseDto(UserPending entity);
}
