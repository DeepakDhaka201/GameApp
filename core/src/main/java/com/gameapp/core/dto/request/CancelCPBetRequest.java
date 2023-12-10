package com.gameapp.core.dto.request;

import lombok.Data;

@Data
public class CancelCPBetRequest {
    private Long periodId;
    private Long betId;
}
