package com.gameapp.wallet;

import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.WalletTransaction;
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
