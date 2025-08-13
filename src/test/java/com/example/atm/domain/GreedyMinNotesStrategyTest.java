package com.example.atm.domain;



import com.example.atm.adapters.GreedyMinNotesStrategy;

import com.example.atm.ports.DispenseStrategy;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GreedyMinNotesStrategyTest {

    private final DispenseStrategy strategy = new GreedyMinNotesStrategy();

    @Test
    void findsPlanWhenExactCombinationExists() {
        Map<Integer, Integer> inv = Map.of(50, 2, 20, 3, 10, 5, 5, 10);
        Optional<DispensePlan> plan = strategy.plan(130, inv);
        assertTrue(plan.isPresent());
        int total = plan.get().amount();
        assertEquals(130, total);
        // Greedy typically: 50x2 + 20x1 + 10x1
        assertEquals(130, plan.get().notes().total());
    }

    @Test
    void returnsEmptyWhenNoCombinationExists() {
        // Only one 20 and one 10; request 40 cannot be formed (needs 20+10+10)
        Map<Integer, Integer> inv = Map.of(20, 1, 10, 1);
        assertTrue(strategy.plan(40, inv).isEmpty());
    }

    @Test
    void returnsEmptyForNonPositiveAmount() {
        Map<Integer, Integer> inv = Map.of(10, 10);
        assertTrue(strategy.plan(0, inv).isEmpty());
        assertTrue(strategy.plan(-30, inv).isEmpty());
    }
}
