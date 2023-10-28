package com.gameapp.service;
import com.gameapp.utils.JWTUtils;
import com.gameapp.core.dto.GenerateOtpResponse;
import com.gameapp.core.dto.request.LoginRequest;
import com.gameapp.corepersistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.gameapp.core.redis.RedisService;
import com.gameapp.corepersistence.repo.UserRepo;
import com.gameapp.core.util.AppError;
import com.gameapp.core.util.AppException;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.gameapp.core.util.Message.INVALID_OTP;
import static com.gameapp.core.util.Message.OTP_EXPIRED;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private static final Long OTP_EXPIRY_TIME = (long) (60 * 1000);
    private final RedisService redisService;

    private final JWTUtils jwtUtils;

    private final UserRepo userRepo;

    private final UserService userService;

    public GenerateOtpResponse generateOtp(String phone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        String secret = UUID.randomUUID().toString();

        System.out.println(otp);
        log.info("Generated Otp : {} and Secret : {} for number : {}", otp, secret, phone);
        //Todo: send to phone

        redisService.set(phone + secret, otp, OTP_EXPIRY_TIME);
        GenerateOtpResponse generateOtpResponse = new GenerateOtpResponse();
        generateOtpResponse.setSecret(secret);
        return generateOtpResponse;
    }

    public String verifyOtp(LoginRequest loginRequest) {
        String phone = loginRequest.getPhone();
        String otp = loginRequest.getOtp();
        String secret = loginRequest.getSecret();

        String redisKey = phone + secret;
        String storedOtp = (String) redisService.get(redisKey);
        if (Objects.nonNull(storedOtp)) {
            if (storedOtp.equalsIgnoreCase(otp)) {
                redisService.delete(redisKey);

                User user = userRepo.findByPhone(phone);
                if (Objects.isNull(user)) {
                    user = userService.createNewUser(phone);
                }

                return jwtUtils.generateToken(user);
            } else {
                throw new AppException(AppError.INVALID_OTP, INVALID_OTP);
            }
        } else {
            throw new AppException(AppError.OTP_EXPIRED, OTP_EXPIRED);
        }
    }
}
