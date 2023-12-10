package com.gameapp.apis;


import com.gameapp.colorprediction.CPService;
import com.gameapp.colorprediction.entity.CpBet;
import com.gameapp.colorprediction.entity.CpPeriod;
import com.gameapp.core.dto.PlaceCPBetRequest;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.request.CancelCPBetRequest;
import com.gameapp.core.dto.response.CancelCPBetResponse;
import com.gameapp.core.dto.response.PlaceCPBetResponse;
import com.gameapp.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cp")
@RequiredArgsConstructor
public class CpGameApi {
    private final JWTUtils jwtUtils;
    private final CPService cpService;

    @GetMapping("/current-period")
    public ResponseEntity<CpPeriod> getCurrentPeriodId() {
        return ResponseEntity.ok(cpService.getCurrentPeriod());
    }

    @GetMapping("/open-bets")
    public ResponseEntity<List<CpBet>> getOpenBets() {
        return ResponseEntity.ok(cpService.getOpenBets(cpService.getCurrentPeriodId()));
    }

    @GetMapping("/short-history")
    public ResponseEntity<List<CpPeriod>> getHistory() {
        return ResponseEntity.ok(cpService.getShortHistory());
    }

    @GetMapping("/full-history")
    public ResponseEntity<List<CpPeriod>> getFullHistory() {
        return ResponseEntity.ok(cpService.getFullHistory());
    }

    @PostMapping("/place-bet")
    public ResponseEntity<PlaceCPBetResponse> placeCPBet(@RequestHeader Map<String, Object> headers,
                                                        @RequestBody PlaceCPBetRequest placeCPBetRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        return ResponseEntity.ok(cpService.placeCPBet(placeCPBetRequest, userDto));
    }

    @PostMapping("/cancel-bet")
    public ResponseEntity<CancelCPBetResponse> cancelCPBet(@RequestHeader Map<String, Object> headers,
                                                           @RequestBody CancelCPBetRequest placeCPBetRequest) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        cpService.cancelCPBet(placeCPBetRequest, userDto);
        return ResponseEntity.ok(null);
    }
}
