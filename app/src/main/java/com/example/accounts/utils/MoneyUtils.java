package com.example.accounts.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * 金额格式化与计算工具，避免 float 精度问题。
 */
public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static String formatYuan(float amount) {
        return String.format(Locale.CHINA, "￥ %.2f", round(amount));
    }

    public static String formatYuanCompact(float amount) {
        return String.format(Locale.CHINA, "￥%.2f", round(amount));
    }

    public static float round(float amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static float subtract(float left, float right) {
        return BigDecimal.valueOf(left)
                .subtract(BigDecimal.valueOf(right))
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static float sum(float... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (float value : values) {
            total = total.add(BigDecimal.valueOf(value));
        }
        return total.setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}
