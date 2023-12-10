package com.gameapp.core.dto;

import lombok.Data;

@Data
public class PlaceCPBetRequest {
    private Long periodId;
    private Double amount;
    private Integer number;
    private CPColor color;
}
