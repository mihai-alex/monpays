package com.monpays.mappers.user;

import com.monpays.dtos.user.UserSignUpDto;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.User;
import com.monpays.entities.user.enums.EUserStatus;

public class UserSignUpDtoMapper {
    public static User UserSignUpDto2User(UserSignUpDto userSignUpDto, String hashedPassword, Profile profile) {

        User user = new User();
        user.setId(0L);
        user.setUserName(userSignUpDto.getUserName());
        user.setPassword(hashedPassword);
        user.setFirstName(userSignUpDto.getFirstName());
        user.setLastName(userSignUpDto.getLastName());
        user.setEmailAddress(userSignUpDto.getEmailAddress());
        user.setAddress(userSignUpDto.getAddress());
        user.setPhoneNumber(userSignUpDto.getPhoneNumber());
        user.setProfile(profile);
        user.setStatus(EUserStatus.ACTIVE);
        return user;
    }
}
