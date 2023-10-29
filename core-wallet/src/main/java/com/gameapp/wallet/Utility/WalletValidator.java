package com.gameapp.wallet.Utility;

import com.gameapp.wallet.model.Wallet;

public class WalletValidator {

    public boolean validateWalletRequest(Wallet wallet) {
        if (wallet.getUser_id() == 0) {
            return false;
        }
        return true;
    }
}
