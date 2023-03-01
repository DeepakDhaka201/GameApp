package com.example.ludo.apis;

import com.example.ludo.dto.BalanceSummary;
import com.example.ludo.dto.TableDto;
import com.example.ludo.dto.UserDto;
import com.example.ludo.dto.response.PlayedBattle;
import com.example.ludo.dto.response.ProfileResponse;
import com.example.ludo.service.DashboardWsService;
import com.example.ludo.service.MyAcService;
import com.example.ludo.service.util.JWTUtils;
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

    private final DashboardWsService dashboardWsService;

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
