package com.example.atm.domain;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public final class Money {
    private final Map<Integer, Integer> notes; // immutable

    public Money(Map<Integer, Integer> notes) {
        Objects.requireNonNull(notes, "notes");
        // Defensive copy + unmodifiable
        this.notes = Collections.unmodifiableMap(notes.entrySet().stream()
                .peek(e -> {
                    if (e.getKey() == null || e.getKey() <= 0) throw new IllegalArgumentException("Invalid denom: " + e.getKey());
                    if (e.getValue() == null || e.getValue() < 0) throw new IllegalArgumentException("Negative count for denom: " + e.getKey());
                })
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Map<Integer, Integer> asMap() {
        return notes;
    }

    /** Calculates the total amount. */
    public int total() {
        return notes.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
    }

    @Override public String toString() { return "Money" + notes; }
}

