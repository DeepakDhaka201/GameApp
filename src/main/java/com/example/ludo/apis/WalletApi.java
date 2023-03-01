package com.example.ludo.apis;

import com.example.ludo.dto.UserDto;
import com.example.ludo.dto.WalletTransaction;
import com.example.ludo.service.WalletService;
import com.example.ludo.service.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/wallet")
@RequiredArgsConstructor
public class WalletApi {
    private final JWTUtils jwtUtils;
    private final WalletService walletService;

    //Todo: add

    //Todo: withdraw

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactionsHistory(@RequestHeader Map<String, Object> headers) {
        UserDto userDto = jwtUtils.verifyToken(headers);
        List<WalletTransaction> transactions = walletService.getWalletTransactions(userDto);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}