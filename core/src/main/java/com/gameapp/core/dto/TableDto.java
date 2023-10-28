package com.gameapp.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableDto {
    private Long tableId;
    private Double amount;
    private Double prize;
    private TableUser creator;
    private TableUser acceptor;
    private TableStatus tableStatus;
}
