package com.gameapp.service;

import com.gameapp.core.dto.GenerateOtpResponse;
import com.gameapp.core.dto.SendOtpApiResponse;
import com.gameapp.core.dto.VerifyOtpApiResponse;
import com.gameapp.core.dto.request.LoginRequest;
import com.gameapp.core.util.AppError;
import com.gameapp.core.util.AppException;
import com.gameapp.corepersistence.entity.User;
import com.gameapp.corepersistence.repo.UserRepo;
import com.gameapp.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static com.gameapp.core.util.Message.INVALID_OTP;
import static com.gameapp.core.util.Message.OTP_NOT_SENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private final JWTUtils jwtUtils;
    private final UserRepo userRepo;
    private final UserService userService;

    private static final String SEND_OTP_URL = "https://2factor.in/API/V1/d7b643bb-d6aa-11eb-8089-0200cd936042/SMS/%s/AUTOGEN/EpicWin"; //1.Phone
    private static final String VERIFY_OTP_URL = "https://2factor.in/API/V1/d7b643bb-d6aa-11eb-8089-0200cd936042/SMS/VERIFY/%s/%s"; //1.SessionId, 2.OtpCode

    public GenerateOtpResponse generateOtp(String phone) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(SEND_OTP_URL, phone);
        SendOtpApiResponse response = restTemplate.getForObject(url, SendOtpApiResponse.class);
        log.info("Response from send otp url: {}", response);
        System.out.println("Response from send otp url: " + response);

        if (Objects.isNull(response) || !response.getStatus().equalsIgnoreCase("Success")) {
            throw new AppException(AppError.OTP_NOT_SENT, OTP_NOT_SENT);
        }

        GenerateOtpResponse generateOtpResponse = new GenerateOtpResponse();
        generateOtpResponse.setSecret(response.getDetails());
        return generateOtpResponse;
    }

    public String verifyOtp(LoginRequest loginRequest) {
        String phone = loginRequest.getPhone();

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(VERIFY_OTP_URL, loginRequest.getSecret(), loginRequest.getOtp());
        VerifyOtpApiResponse response = restTemplate.getForObject(url, VerifyOtpApiResponse.class);
        log.info("Response from verify otp url: {}", response);
        assert response != null;
        if (response.getStatus().equalsIgnoreCase("Success")) {
            User user = userRepo.findByPhone(phone);
            if (Objects.isNull(user)) {
                user = userService.createNewUser(phone, loginRequest.getRefereeCode());
            }
            return jwtUtils.generateToken(user);
        } else {
            throw new AppException(AppError.INVALID_OTP, INVALID_OTP);
        }
    }
}
