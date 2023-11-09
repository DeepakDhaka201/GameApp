package com.gameapp.core.dto;

import lombok.Getter;

@Getter
public enum AppGame {
    LUDO_CLASSIC,
    LUDO_POPULAR,
    LUDO_QUICK,
    COLOR_PREDICTION,
    AVIATOR;

    public static CommissionLevel getCommisionLevel(AppGame game, Double amount) {
        switch (game) {
            case LUDO_CLASSIC:
            case LUDO_POPULAR:
            case LUDO_QUICK:
                return CommissionLevel.PERCENT_5;
        }
        return CommissionLevel.PERCENT_0;
    }
}
