package com.gameapp.colorprediction.entity;

import com.gameapp.core.dto.CPBetStatus;
import com.gameapp.core.dto.CPColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class CpBet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;
    private Long periodId;
    private Double amount;
    private Double prize;

    @Enumerated(EnumType.STRING)
    private CPColor color;
    private Integer number;
    private Long resultTime;

    private Long createdAt;
    private Long updatedAt;

    @Enumerated(EnumType.STRING)
    private CPBetStatus status;

    private String result;
}
