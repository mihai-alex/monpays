package com.monpays.dtos.user;

import com.monpays.entities.user.enums.EUserStatus;
import lombok.Data;

@Data
public class UserHistoryEntryDto {

    private String userName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String address;
    private String profileName;
    private EUserStatus status;
}
