package com.example.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.accounts.utils.MoneyUtils;

import org.junit.Test;

public class MoneyUtilsTest {

    @Test
    public void round_handlesFloatPrecision() {
        assertEquals(0.3f, MoneyUtils.round(0.1f + 0.2f), 0.0001f);
    }

    @Test
    public void subtract_returnsScaledValue() {

        assertEquals(99.99f, MoneyUtils.subtract(100f, 0.01f), 0.0001f);
    }

    @Test
    public void formatYuan_containsCurrencySymbol() {
        assertTrue(MoneyUtils.formatYuan(12.5f).contains("￥"));
        assertTrue(MoneyUtils.formatYuan(12.5f).contains("12.50"));
    }

    @Test
    public void sum_addsMultipleValues() {
        assertEquals(6f, MoneyUtils.sum(1f, 2f, 3f), 0.0001f);
    }
}
