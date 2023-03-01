package com.example.ludo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayedBattle {
    private Long id;
    private Long timestamp;
    private Double prize;
    private BattleResult result;
}