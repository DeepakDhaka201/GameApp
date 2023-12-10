package com.gameapp.apis;

import com.gameapp.core.dto.*;
import com.gameapp.ludo.service.LudoGameService;
import com.gameapp.ludo.service.LudoKingRoomCodeResponse;
import com.gameapp.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/ludo")
@RequiredArgsConstructor
public class LudoGameApi {
    private final JWTUtils jwtUtils;
    private final LudoGameService ludoGameService;

    @PostMapping("/create-table")
    public void createLudoTable(@RequestHeader Map<String, Object> headers,
                                                 @RequestBody CreateLudoTableRequest createLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.createTable(userDto, createLudoTableRequest);
    }

    @PostMapping("/join-table")
    public void joinLudoTable(@RequestHeader Map<String, Object> headers,
                              @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.requestJoinLudoTable(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/delete-table")
    public void deleteLudoTable(@RequestHeader Map<String, Object> headers,
                                @RequestBody DeleteLudoTableRequest deleteLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.deleteLudoTable(deleteLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/accept-table")
    public void acceptJoinLudoTableRequest(@RequestHeader Map<String, Object> headers,
                                           @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.acceptJoinLudoTableRequest(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/reject-table")
    public void rejectJoinLudoTableRequest(@RequestHeader Map<String, Object> headers,
                                           @RequestBody JoinLudoTableRequest joinLudoTableRequest) throws InterruptedException {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.rejectJoinLudoTableRequest(joinLudoTableRequest.getTableId(), userDto);
    }

    @PostMapping("/update-table-result")
    public void updateResult(@RequestHeader Map<String, Object> headers,
                             @RequestBody UpdateResultRequest updateResultRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ludoGameService.updateTableResult(updateResultRequest, userDto);
    }

    @PostMapping("/table-room-code")
    public ResponseEntity<LudoKingRoomCodeResponse> getRoomCode(@RequestHeader Map<String, Object> headers,
                                                                @RequestBody JoinLudoTableRequest joinLudoTableRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        String roomCode = ludoGameService.getRoomCode(joinLudoTableRequest.getTableId(), userDto);
        LudoKingRoomCodeResponse response = new LudoKingRoomCodeResponse();
        response.setRoomCode(roomCode);
        return ResponseEntity.ok(response);
    }
}