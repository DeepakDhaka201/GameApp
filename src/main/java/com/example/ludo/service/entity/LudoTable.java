package com.example.ludo.service.entity;

import com.example.ludo.dto.LudoType;
import com.example.ludo.dto.TableStatus;
import com.example.ludo.dto.UserGameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class LudoTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String createdBy;
    private String acceptedBy;

    @Enumerated(EnumType.STRING)
    private LudoType ludoType;

    private Double amount;
    private Double prize;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    private String roomCode;
    private Double creatorBalanceDeducted;
    private Double acceptorBalanceDeducted;

    private Long createdAt;
    private Long requestAcceptedAt;

    @Enumerated(EnumType.STRING)
    private UserGameStatus creatorResult;
    @Enumerated(EnumType.STRING)
    private UserGameStatus acceptorResult;
    private String creatorScreenshot;
    private String acceptorScreenshot;
    private String creatorReason;
    private String acceptorReason;
    private Long creatorResultUpdatedAt;
    private Long acceptorResultUpdatedAt;
    private String cancelledBy;
    private String winner;
    private Boolean balanceSettled;
    private Long updatedAt;
}