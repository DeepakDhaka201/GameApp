package com.example.ludo.service;

import com.example.ludo.dto.UserDto;
import com.example.ludo.dto.WalletTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    public List<WalletTransaction> getWalletTransactions(UserDto userDto) {
        return null;
    }
}
