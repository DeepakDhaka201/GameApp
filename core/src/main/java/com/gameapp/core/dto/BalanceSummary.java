package com.gameapp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSummary {
    private Double balance;
    private Double onHold;
    private Double totalWinning;
    private Double totalLost;
    private Double referralEarning;
}