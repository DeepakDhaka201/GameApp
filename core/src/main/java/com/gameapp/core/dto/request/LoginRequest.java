package com.gameapp.core.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String phone;
    private String otp;
    private String secret;
}