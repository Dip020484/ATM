package com.example.atm.domain;



import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void totalShouldSumDenominationTimesCount() {
        Money m = new Money(Map.of(50, 2, 20, 3, 5, 1)); // 100 + 60 + 5 = 165
        assertEquals(165, m.total());
    }

    @Test
    void invalidDenominationOrCountShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Money(Map.of(-10, 1)));
        assertThrows(IllegalArgumentException.class, () -> new Money(Map.of(10, -1)));
    }

    @Test
    void asMapIsImmutableSnapshot() {
        Money m = new Money(Map.of(10, 1));
        var map = m.asMap();
        assertThrows(UnsupportedOperationException.class, () -> map.put(5, 1));
    }
}

