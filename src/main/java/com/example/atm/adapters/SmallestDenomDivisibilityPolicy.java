package com.example.atm.adapters;


import com.example.atm.ports.AmountPolicy;

import static com.example.atm.errors.Errors.InvalidAmountException;

import java.util.Set;

public final class SmallestDenomDivisibilityPolicy implements AmountPolicy {
    private final int smallestDenom;

    public SmallestDenomDivisibilityPolicy(Set<Integer> denominations) {
        if (denominations == null || denominations.isEmpty()) throw new IllegalArgumentException("No denominations");
        this.smallestDenom = denominations.stream().mapToInt(Integer::intValue).min().orElseThrow();
    }

    @Override public void validate(int amount) {
        if (amount <= 0) throw new InvalidAmountException("Amount must be positive");
        if (amount % smallestDenom != 0) {
            throw new InvalidAmountException("Amount must be multiple of " + smallestDenom);
        }
    }
}
