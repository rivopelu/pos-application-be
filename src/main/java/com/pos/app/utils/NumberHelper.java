package com.pos.app.utils;

import java.math.BigInteger;

public class NumberHelper {

    public static BigInteger getPercentageTotal(BigInteger percentage, BigInteger num) {
        return num.multiply(percentage).divide(BigInteger.valueOf(100));

    }
}
