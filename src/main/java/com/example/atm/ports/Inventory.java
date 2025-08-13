package com.example.atm.ports;


import java.util.Map;
import java.util.Set;

/** Abstraction for note storage. */
public interface Inventory {
    /** Snapshot current inventory (denomination → count). */
    Map<Integer, Integer> snapshot();

    /** Add notes to inventory (idempotent for zero entries). */
    void add(Map<Integer, Integer> deposit);

    /** Remove notes (must have enough, otherwise throw IllegalStateException). */
    void remove(Map<Integer, Integer> take);

    /** Supported denominations. */
    Set<Integer> denominations();

    /** Total balance (sum(denom*count)). */
    int balance();
}

