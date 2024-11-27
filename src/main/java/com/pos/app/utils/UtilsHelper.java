package com.pos.app.utils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    private static LocalDate getCurrentDateInJakarta() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toLocalDate();
    }
}
