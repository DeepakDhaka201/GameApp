package com.example.ludo.service.util;

import java.util.stream.Stream;

import static com.example.ludo.service.util.Constant.GAME_LOCK_KEY;

public class AppUtils {
    public static String getRandomString(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String getTableLockKey(Long tableId) {
        return getLockKey("table", String.valueOf(tableId));
    }

    public static String getWalletLockKey(String phone) {
        return getLockKey("wallet", phone);
    }

    public static String getLockKey(String ...keys) {
        if (keys.length == 0) {
            throw new AppException("lock key can not be empty");
        }

        String key = null;
        for (String _key : keys) {
            key = key + "_" + _key;
        }

        return key;
    }
 }
