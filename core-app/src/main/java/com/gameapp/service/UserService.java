package com.gameapp.service;

import com.gameapp.core.dto.*;
import com.gameapp.core.util.AppException;
import com.gameapp.corepersistence.entity.User;
import com.gameapp.corepersistence.entity.UserBalance;
import com.gameapp.corepersistence.repo.UserBalanceRepo;
import com.gameapp.corepersistence.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gameapp.core.util.AppUtils.getRandomString;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserBalanceRepo userBalanceRepo;

    @Transactional
    public User createNewUser(String phone, String refereeCode) {
        String userId = getRandomString(8);

        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setUserName(getRandomString(6));
        user.setRole(UserRole.USER);
        user.setKycStatus(KycStatus.PENDING);
        user.setReferralCode(getRandomString(8).toUpperCase());
        user.setOnReferralTier(ReferralTier.SILVER);

        if (refereeCode != null) {
            User referee = userRepo.findByReferralCode(refereeCode);
            if (referee != null) {
                user.setReferredBy(referee.getId());
                user.setReferredTier(referee.getOnReferralTier());
            }
        }

        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(userId);

        userRepo.save(user);
        userBalanceRepo.save(userBalance);
        return user;
    }

    public void addReferralCode(UserDto userDto, AddReferralRequest referralRequest) {
        String userId = userDto.getLoggedInUserId();
        String refereeCode = referralRequest.getReferralCode();

        Optional<User> optionalUser = userRepo.findById(userId)
        optionalUser.ifPresent(user -> {
            if (user.getReferralCode() != null) {
                throw new AppException("Referral code already added");
            }
            User referee = userRepo.findByReferralCode(refereeCode);
            if (referee == null) {
                throw new AppException("Invalid referral code");
            }
            user.setReferredBy(referee.getId());
            user.setReferredTier(referee.getOnReferralTier());
            user.setReferralCode(referralRequest.getReferralCode());
        });
        optionalUser.orElseThrow(() -> new AppException("User not found"));
    }
}