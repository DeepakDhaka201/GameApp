package com.gameapp.core.dto;

import lombok.Getter;


@Getter
public enum ReferralTier {
    DIAMOND(50.0),
    PLATINUM(40.0),
    GOLD(30.0),
    SILVER(20.0),
    BRONZE(10.0);

    private final Double commissionPercentage;

    ReferralTier(Double commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

}
