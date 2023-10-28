package com.gameapp.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateResultRequest {
    private Long tableId;
    private UserGameStatus status;
    private String screenshotUrl;
    private String reason;
}