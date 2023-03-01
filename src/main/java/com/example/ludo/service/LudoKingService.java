package com.example.ludo.service;

import com.example.ludo.service.util.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LudoKingService {
    private final RestTemplate restTemplate;
    public static final String CREATE_ROOM_API = "http://165.22.211.82:12300/v1/rooms/new-code-api";

    public String getLudoClassicRoomCode() {
        try {
            LudoKingRoomCodeResponse response = restTemplate.getForObject(
                    CREATE_ROOM_API, LudoKingRoomCodeResponse.class);
            if (Objects.isNull(response) || Objects.isNull(response.getRoomCode())) {
                throw new AppException("Getting Null Response");
            }
            return response.getRoomCode();
        } catch (Exception e) {
            log.error("Getting error when fetching ludo king room code : {}", e);
            return null;
        }
    }
}
