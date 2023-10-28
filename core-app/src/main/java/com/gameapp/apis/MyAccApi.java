package com.gameapp.apis;

import com.gameapp.core.dto.BalanceSummary;
import com.gameapp.core.dto.TableDto;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.response.PlayedBattle;
import com.gameapp.core.dto.response.ProfileResponse;
import com.gameapp.ludo.service.LudoLobbyWsService;
import com.gameapp.service.MyAcService;
import com.gameapp.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
public class MyAccApi {
    private final JWTUtils jwtUtils;
    private final MyAcService myAcService;

    private final LudoLobbyWsService dashboardWsService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@RequestHeader Map<String, Object> headers) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        ProfileResponse profileResponse = myAcService.getProfile(userDto);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @GetMapping("/battles")
    public ResponseEntity<List<PlayedBattle>> getPlayedBattles(@RequestHeader Map<String, Object> headers) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        List<PlayedBattle> playedBattles = myAcService.getPlayedBattles(userDto);
        return new ResponseEntity<>(playedBattles, HttpStatus.OK);
    }

    @GetMapping("/balance-summary")
    public ResponseEntity<BalanceSummary> getBalanceSummary(@RequestHeader Map<String, Object> headers) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        BalanceSummary balanceSummary = myAcService.getBalanceSummary(userDto);
        return new ResponseEntity<>(balanceSummary, HttpStatus.OK);
    }

    @GetMapping("/ludo/dashboard")
    public ResponseEntity<List<TableDto>> getVisibleTables() {
        return ResponseEntity.ok(dashboardWsService.collectVisibleTables());
    }
}
