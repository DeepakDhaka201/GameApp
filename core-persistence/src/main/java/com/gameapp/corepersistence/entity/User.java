package com.gameapp.corepersistence.entity;

import com.gameapp.core.dto.KycStatus;
import com.gameapp.core.dto.ReferralTier;
import com.gameapp.core.dto.UserRole;
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

    @Enumerated(EnumType.STRING)
    private ReferralTier referredTier;

    @Enumerated(EnumType.STRING)
    private ReferralTier onReferralTier;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String referredBy;

    private Long createdAt;
    private Long updatedAt;
}
