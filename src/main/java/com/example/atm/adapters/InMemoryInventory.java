package com.example.atm.adapters;



import com.example.atm.ports.Inventory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/** Thread-safe in-memory inventory (TreeMap DESC for reporting). */
public final class InMemoryInventory implements Inventory {
    private final NavigableMap<Integer, Integer> store = new TreeMap<>(Comparator.reverseOrder());
    private final ReentrantLock lock = new ReentrantLock();

    public InMemoryInventory(Map<Integer, Integer> initial) {
        Objects.requireNonNull(initial, "initial");
        initial.forEach((d,c) -> {
            if (d == null || d <= 0) throw new IllegalArgumentException("Invalid denom: " + d);
            if (c == null || c < 0) throw new IllegalArgumentException("Negative count for denom: " + d);
        });
        lock.lock();
        try {
            initial.forEach((d,c) -> { if (c > 0) store.put(d, c); });
        } finally { lock.unlock(); }
    }

    @Override public Map<Integer, Integer> snapshot() {
        lock.lock();
        try {
            return store.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } finally { lock.unlock(); }
    }

    @Override public void add(Map<Integer, Integer> deposit) {
        Objects.requireNonNull(deposit, "deposit");
        lock.lock();
        try {
            deposit.forEach((d,c) -> {
                if (c == null || c < 0) throw new IllegalArgumentException("Negative deposit for denom: " + d);
                if (c == 0) return;
                store.merge(d, c, Integer::sum);
            });
        } finally { lock.unlock(); }
    }

    @Override public void remove(Map<Integer, Integer> take) {
        Objects.requireNonNull(take, "take");
        lock.lock();
        try {
            // Pre-check
            for (Map.Entry<Integer,Integer> e : take.entrySet()) {
                int have = store.getOrDefault(e.getKey(), 0);
                if (e.getValue() < 0) throw new IllegalArgumentException("Negative remove");
                if (e.getValue() > have) throw new IllegalStateException("Not enough " + e.getKey() + " notes");
            }
            // Apply
            take.forEach((d,c) -> {
                if (c == 0) return;
                int remaining = store.get(d) - c;
                if (remaining == 0) store.remove(d); else store.put(d, remaining);
            });
        } finally { lock.unlock(); }
    }

    @Override public Set<Integer> denominations() {
        lock.lock();
        try {
            return new LinkedHashSet<>(store.keySet());
        } finally { lock.unlock(); }
    }

    @Override public int balance() {
        lock.lock();
        try {
            return store.entrySet().stream().mapToInt(e -> e.getKey() * e.getValue()).sum();
        } finally { lock.unlock(); }
    }
}

