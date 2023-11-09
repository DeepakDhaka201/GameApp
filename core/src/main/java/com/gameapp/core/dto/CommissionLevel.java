package com.gameapp.core.dto;

import lombok.Getter;

@Getter
public enum CommissionLevel {
    PERCENT_0(0.0),
    PERCENT_1(1),
    PERCENT_2(2),
    PERCENT_2_5(2.5),
    PERCENT_3(3),
    PERCENT_4(4),
    PERCENT_5(5),
    PERCENT_6(6),
    PERCENT_7(7),
    PERCENT_7_5(7.5),
    PERCENT_8(8),
    PERCENT_9(9),
    PERCENT_10(10);

    private final double commission;
    CommissionLevel(double commission) {
        this.commission = commission;
    }

    public static CommissionLevel getCommissionLevel(double value) {
        for (CommissionLevel commissionLevel : CommissionLevel.values()) {
            if (commissionLevel.getCommission() == value) {
                return commissionLevel;
            }
        }
        return null;
    }
}
