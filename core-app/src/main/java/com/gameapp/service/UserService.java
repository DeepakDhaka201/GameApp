package com.gameapp.service;

import com.gameapp.core.dto.KycStatus;
import com.gameapp.core.dto.UserRole;
import com.gameapp.corepersistence.entity.User;
import com.gameapp.corepersistence.entity.UserBalance;
import com.gameapp.corepersistence.repo.UserBalanceRepo;
import com.gameapp.corepersistence.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gameapp.core.util.AppUtils.getRandomString;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserBalanceRepo userBalanceRepo;

    @Transactional
    public User createNewUser(String phone) {
        String userId = getRandomString(8);

        User user = new User();
        user.setId(userId);
        user.setPhone(phone);
        user.setUserName(getRandomString(6));
        user.setRole(UserRole.USER);
        user.setKycStatus(KycStatus.PENDING);
        user.setReferralCode(getRandomString(6).toUpperCase());
        user.setReferralCommision(1.0);

        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(userId);

        userRepo.save(user);
        userBalanceRepo.save(userBalance);
        return user;
    }
}