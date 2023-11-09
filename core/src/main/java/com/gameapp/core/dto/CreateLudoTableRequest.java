package com.gameapp.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateLudoTableRequest {
    private AppGame type;
    private Double amount;
}