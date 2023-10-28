package com.gameapp.core.dto.response;

import com.gameapp.core.dto.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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