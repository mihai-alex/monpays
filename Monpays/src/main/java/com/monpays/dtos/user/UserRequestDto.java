package com.monpays.dtos.user;

import jakarta.persistence.Version;
import lombok.Data;

@Data
public class UserRequestDto {
    private String userName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String address;
    private String profileName;
    @Version
    private Long version;

    private boolean mfaEnabled = false;
}
