package com.example.ludo.apis;

import com.example.ludo.dto.*;
import com.example.ludo.service.LudoGameService;
import com.example.ludo.service.LudoKingRoomCodeResponse;
import com.example.ludo.service.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/ludo/table")
@RequiredArgsConstructor
public class LudoGameApi {
    private final JWTUtils jwtUtils;
    private final LudoGameService ludoGameService;

    @PostMapping("/create")
    public void createLudoTable(@RequestHeader Map<String, Object> headers,
                                                 @RequestBody CreateLudoTableRequest createLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.createTable(userDto, createLudoTableRequest);
    }

    @PostMapping("/join")
    public void joinLudoTable(@RequestHeader Map<String, Object> headers,
                              @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.requestJoinLudoTable(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/delete")
    public void deleteLudoTable(@RequestHeader Map<String, Object> headers,
                                @RequestBody DeleteLudoTableRequest deleteLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.deleteLudoTable(deleteLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/accept")
    public void acceptJoinLudoTableRequest(@RequestHeader Map<String, Object> headers,
                                           @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.acceptJoinLudoTableRequest(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/reject")
    public void rejectJoinLudoTableRequest(@RequestHeader Map<String, Object> headers,
                                           @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.rejectJoinLudoTableRequest(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/update-result")
    public void updateResult(@RequestHeader Map<String, Object> headers,
                             @RequestBody UpdateResultRequest updateResultRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.updateTableResult(updateResultRequest, userDto);
    }

    @PostMapping("/room-code")
    public ResponseEntity<LudoKingRoomCodeResponse> getRoomCode(@RequestHeader Map<String, Object> headers,
                                                                @RequestBody JoinLudoTableRequest joinLudoTableRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        String roomCode = ludoGameService.getRoomCode(joinLudoTableRequest.getTableId(), userDto);
        LudoKingRoomCodeResponse response = new LudoKingRoomCodeResponse();
        response.setRoomCode(roomCode);
        return ResponseEntity.ok(response);
    }
}