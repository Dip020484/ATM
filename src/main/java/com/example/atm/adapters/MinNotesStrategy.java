package com.example.atm.adapters;



import com.example.atm.domain.DispensePlan;
import com.example.atm.domain.Money;
import com.example.atm.ports.DispenseStrategy;

import java.util.*;

/** try highest denominations first; respects limited stock. */
public final class MinNotesStrategy implements DispenseStrategy {
    @Override
    public Optional<DispensePlan> plan(int amount, Map<Integer, Integer> inventory) {
        if (amount <= 0) return Optional.empty();

        // Sort denominations DESC to minimize note count
        List<Integer> denoms = new ArrayList<>(inventory.keySet());
        denoms.sort(Comparator.reverseOrder());

        int remaining = amount;
        Map<Integer, Integer> use = new LinkedHashMap<>();

        for (int d : denoms) {
            if (remaining <= 0) break;
            int available = inventory.getOrDefault(d, 0);
            if (available <= 0 || d > remaining) continue;

            int need = Math.min(remaining / d, available);
            if (need > 0) {
                use.put(d, need);
                remaining -= need * d;
            }
        }

        if (remaining == 0) {
            return Optional.of(new DispensePlan(new Money(use)));
        }
        return Optional.empty();
    }
}

