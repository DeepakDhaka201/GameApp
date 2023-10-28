package com.gameapp.corepersistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;
    private Double balance = 0.0;
    private Double totalWithdrawal = 0.0;
    private Double totalWon = 0.0;
    private Double totalReferral = 0.0;
    private Double totalCommision = 0.0;
    private Double onHold = 0.0;
    private Double withdrawalRequested = 0.0;
}
