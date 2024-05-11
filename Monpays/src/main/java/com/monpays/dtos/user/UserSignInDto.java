package com.monpays.dtos.user;

import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInDto {
    @Version
    private Long version;

    private String userName;
    private String password;
}
