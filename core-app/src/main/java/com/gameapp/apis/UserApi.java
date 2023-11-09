package com.gameapp.apis;

import com.gameapp.core.dto.AddReferralRequest;
import com.gameapp.core.dto.GenerateOtpResponse;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.request.LoginRequest;
import com.gameapp.core.dto.response.LoginResponse;
import com.gameapp.service.OtpService;
import com.gameapp.service.UserService;
import com.gameapp.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserApi {

    private final JWTUtils jwtUtils;
    private final OtpService otpService;
    private final UserService userService;

    @CrossOrigin
    @PostMapping("/send-otp")
    public ResponseEntity<GenerateOtpResponse> sendOtp(@RequestBody LoginRequest loginRequest) {
        log.info("Sending otp for : {}", loginRequest);
        final String phone = loginRequest.getPhone();
        return ResponseEntity.ok(otpService.generateOtp(phone));
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = otpService.verifyOtp(loginRequest);
        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }

    @CrossOrigin
    @PostMapping("/logout")
    public void logout(@RequestHeader Map<String, Object> headers) {
        jwtUtils.verifyToken(headers);
    }

    @CrossOrigin
    @PostMapping("/add-referral")
    public ResponseEntity.BodyBuilder addReferralCode(@RequestHeader Map<String, Object> headers,
                                @RequestBody AddReferralRequest referralRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        userService.addReferralCode(userDto, referralRequest);
        return ResponseEntity.ok();
    }
}