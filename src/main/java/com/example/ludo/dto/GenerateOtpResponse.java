package com.example.ludo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenerateOtpResponse {
    private String secret;
}
