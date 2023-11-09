package com.gameapp.corepersistence.entity;

import com.gameapp.core.dto.PaymentMethodStatus;
import com.gameapp.core.dto.PaymentMethodType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Data
public class PaymentMethodEntity {
    @Id
    String id;
    String userId;
    @Enumerated(EnumType.STRING)
    PaymentMethodType type;
    @Enumerated(EnumType.STRING)
    PaymentMethodStatus status;
    String upi;
    String accountNumber;
    String ifscCode;
    String accountHolderName;
    String bankName;
}
