package com.example.ludo.apis;

import com.example.ludo.dto.GenerateOtpResponse;
import com.example.ludo.dto.request.LoginRequest;
import com.example.ludo.dto.response.LoginResponse;
import com.example.ludo.service.OtpService;
import com.example.ludo.service.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserApi {

    private final JWTUtils jwtUtils;
    private final OtpService otpService;

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
}