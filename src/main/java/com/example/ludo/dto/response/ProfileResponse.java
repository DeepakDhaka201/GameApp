package com.example.ludo.dto.response;

import com.example.ludo.dto.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ProfileResponse {
    private String username;
    private String phone;
    private String avatar;
    private Double totalWin;
    private int playedBattles;
    private KycStatus kycStatus;
}