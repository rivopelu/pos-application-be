package com.pos.app.utils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class UtilsHelper {
    private static LocalDate lastResetDate = getCurrentDateInJakarta();

    public static BigInteger generateOrderCode(BigInteger latestCode) {
        if (!getCurrentDateInJakarta().isEqual(lastResetDate)) {
            lastResetDate = getCurrentDateInJakarta();
            return BigInteger.valueOf(1000);
        }

        if (latestCode == null || latestCode.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.valueOf(1000);
        }

        return latestCode.add(BigInteger.ONE);
    }

    public static String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int passwordLength = 8;
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }

    private static LocalDate getCurrentDateInJakarta() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toLocalDate();
    }

    public static Long addDaysUnixTime(Long currentDate, BigInteger durationDay) {
        return currentDate + (7 * 24 * 60 * 60);
    }

}