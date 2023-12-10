package com.gameapp.core.dto;

import lombok.Getter;


@Getter
public enum AppGame {
    LUDO_KING,
    COLOR_PREDICTION,
    AVIATOR;

    public static CommissionLevel getLudoCommisionLevel(LudoType ludoType, Double amount) {
        switch (ludoType) {
            case LUDO_KING_CLASSIC:
            case LUDO_KING_POPULAR:
            case LUDO_KING_QUICK:
                return CommissionLevel.PERCENT_5;
        }
        return CommissionLevel.PERCENT_0;
    }
}
