package com.monpays.dtos.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVerificationRequest {
    private String userName;
    private String code;
}
