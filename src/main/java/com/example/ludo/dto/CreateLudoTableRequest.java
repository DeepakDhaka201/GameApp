package com.example.ludo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateLudoTableRequest {
    private LudoType type;
    private Double amount;
}