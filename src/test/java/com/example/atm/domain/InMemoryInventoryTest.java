package com.example.atm.domain;



import com.example.atm.adapters.InMemoryInventory;
import com.example.atm.ports.Inventory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryInventoryTest {

    @Test
    void addRemoveAndBalanceWork() {
        Inventory inv = new InMemoryInventory(Map.of(50, 2, 20, 1)); // 120
        assertEquals(120, inv.balance());

        inv.add(Map.of(10, 3)); // +30
        assertEquals(150, inv.balance());

        inv.remove(Map.of(50, 1, 10, 1)); // -60
        assertEquals(90, inv.balance());
    }

    @Test
    void removeThrowsWhenInsufficientParticularDenomination() {
        Inventory inv = new InMemoryInventory(Map.of(20, 1));
        assertThrows(IllegalStateException.class, () -> inv.remove(Map.of(20, 2)));
    }

    @Test
    void snapshotIsIndependentCopy() {
        Inventory inv = new InMemoryInventory(Map.of(10, 2));
        var snap = inv.snapshot();
        // Mutating the snapshot should not affect the store
        snap.put(10, 999);
        assertEquals(20, inv.balance());
    }
}
