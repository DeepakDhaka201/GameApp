package com.gameapp.ludo.service;

import com.gameapp.core.dto.AppGame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.gameapp.core.util.AppException;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LudoKingService {
    private final RestTemplate restTemplate;
    public static final String CREATE_CLASSIC_ROOM_API = "https://ludo-king-room-code-api.p.rapidapi.com/roomcode/c";
    public static final String CREATE_POPULAR_ROOM_API = "https://ludo-king-room-code-api.p.rapidapi.com/roomcode/p";
    public static final String CREATE_QUICK_ROOM_API = "https://ludo-king-room-code-api.p.rapidapi.com/roomcode/q";


    public String getLudoKingRoomCode(AppGame appGame) {
        String url = null;
        switch (appGame) {
            case LUDO_CLASSIC:
                url = CREATE_CLASSIC_ROOM_API;
                break;
            case LUDO_POPULAR:
                url = CREATE_POPULAR_ROOM_API;
                break;
            case LUDO_QUICK:
                url = CREATE_QUICK_ROOM_API;
                break;
        }
        if (Objects.isNull(url)) {
            throw new AppException("Invalid AppGame type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "Bearer your_access_token");
        headers.set("X-RapidAPI-Host", "header_value");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<LudoKingRoomCodeResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, LudoKingRoomCodeResponse.class);
        if (response.getStatusCode().isError()) {
            throw new AppException("Unable to create room");
        }
        return Objects.requireNonNull(response.getBody()).getRoomCode();
    }
}
