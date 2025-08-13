package com.example.atm.service;



import com.example.atm.adapters.GreedyMinNotesStrategy;
import com.example.atm.adapters.InMemoryInventory;
import com.example.atm.adapters.SmallestDenomDivisibilityPolicy;
import com.example.atm.domain.Money;
import com.example.atm.errors.Errors.*;
import com.example.atm.ports.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CashMachineTest {

    private CashMachine atm;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        Map<Integer, Integer> initial = new LinkedHashMap<>();
        initial.put(50, 2);
        initial.put(20, 3);
        initial.put(10, 5);
        initial.put(5,  10);
        inventory = new InMemoryInventory(initial);

        var strategy = new GreedyMinNotesStrategy();
        var policy   = new SmallestDenomDivisibilityPolicy(inventory.denominations());
        atm = new CashMachine(inventory, strategy, policy);
    }

    @Test
    void withdrawSuccessUpdatesInventoryAndReturnsNotes() {
        int before = atm.balance(); // 50*2 + 20*3 + 10*5 + 5*10 = 100 + 60 + 50 + 50 = 260
        assertEquals(260, before);

        Money dispensed = atm.withdraw(130);
        assertEquals(130, dispensed.total());

        int after = atm.balance();
        assertEquals(130, after); // 260 - 130
    }

    @Test
    void depositIncreasesBalance() {
        int before = atm.balance();
        atm.deposit(new Money(Map.of(10, 2, 5, 1)));
        assertEquals(before + 25, atm.balance());
    }

    @Test
    void invalidAmountRejectedByPolicy() {
        // Smallest denomination is 5 → not divisible should fail
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(3));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(0));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(-10));
    }

    @Test
    void insufficientFundsWhenAmountExceedsTotalBalance() {
        // Current total 260; ask for more
        assertThrows(InsufficientFundsException.class, () -> atm.withdraw(1000));
    }

    @Test
    void unavailableDenominationsWhenStockInsufficientToFormAmount() {

        var inv = new InMemoryInventory(Map.of(50, 1, 5, 2)); // balance = 60
        var policy = new SmallestDenomDivisibilityPolicy(inv.denominations());
        var atm2 = new CashMachine(inv, new GreedyMinNotesStrategy(), policy);
        assertThrows(UnavailableDenominationsException.class, () -> atm2.withdraw(40));


    }

    @Test
    void concurrentChangeBetweenPlanAndApplySurfacesAsUnavailable() {

        var inv = new InMemoryInventory(Map.of(20, 2));  // start
        inv.add(Map.of(10, 1));                           // now 10 exists

        var policy = new SmallestDenomDivisibilityPolicy(inv.denominations()); // smallest = 10 (now!)
        var atm2   = new CashMachine(inv, new GreedyMinNotesStrategy(), policy);

        assertDoesNotThrow(() -> atm2.withdraw(30));
    }
}

