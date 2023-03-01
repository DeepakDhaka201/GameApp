package com.example.ludo.service.entity;

import com.example.ludo.dto.KycStatus;
import com.example.ludo.dto.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    private String userName;
    private String phone;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private String avatar;
    private String referralCode;
    private Double referralCommision;
    private String upiId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String referredBy;
    private Long createdAt;
    private Long updatedAt;
}
