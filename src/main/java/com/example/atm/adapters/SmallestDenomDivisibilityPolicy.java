package com.example.atm.adapters;

import com.example.atm.ports.AmountPolicy;

import static com.example.atm.errors.Errors.InvalidAmountException;

import java.util.Objects;
import java.util.Set;

/**
 * Validates withdrawal amounts against the smallest available denomination and
 * enforces that all supported denominations are multiples of 10 (×10 system).
 *
 * - Amount must be > 0
 * - Amount must be divisible by the smallest denomination
 * - All denominations must be positive and divisible by 10
 */
public final class SmallestDenomDivisibilityPolicy implements AmountPolicy {
    private final int smallestDenom;

    public SmallestDenomDivisibilityPolicy(Set<Integer> denominations) {
        Objects.requireNonNull(denominations, "denominations");
        if (denominations.isEmpty()) throw new IllegalArgumentException("No denominations");

        // Enforce ×10 denominations (e.g., 10, 20, 50, 100, ...)
        boolean hasNonMultipleOf10 = denominations.stream()
                .anyMatch(d -> d == null || d <= 0 || d % 10 != 0);
        if (hasNonMultipleOf10) {
            throw new IllegalArgumentException("All denominations must be positive multiples of 10");
        }

        this.smallestDenom = denominations.stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElseThrow();
    }

    @Override
    public void validate(int amount) {
        if (amount <= 0) throw new InvalidAmountException("Amount must be positive");
        if (amount % smallestDenom != 0) {
            throw new InvalidAmountException("Amount must be a multiple of " + smallestDenom);
        }
    }
}
