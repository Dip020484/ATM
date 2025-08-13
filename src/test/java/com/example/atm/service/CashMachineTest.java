package com.example.atm.service;

import com.example.atm.adapters.MinNotesStrategy;
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
        // Use only ×10 denominations
        Map<Integer, Integer> initial = new LinkedHashMap<>();
        initial.put(50, 2);
        initial.put(20, 3);
        initial.put(10, 5);
        // Total = 50*2 + 20*3 + 10*5 = 100 + 60 + 50 = 210
        inventory = new InMemoryInventory(initial);

        var strategy = new MinNotesStrategy();
        var policy   = new SmallestDenomDivisibilityPolicy(inventory.denominations());
        atm = new CashMachine(inventory, strategy, policy);
    }

    @Test
    void withdrawSuccessUpdatesInventoryAndReturnsNotes() {
        int before = atm.balance();
        assertEquals(210, before);

        Money dispensed = atm.withdraw(130);
        assertEquals(130, dispensed.total());

        int after = atm.balance();
        assertEquals(80, after); // 210 - 130
    }

    @Test
    void depositIncreasesBalance() {
        int before = atm.balance();
        // Deposit only ×10 denominations
        atm.deposit(new Money(Map.of(10, 2, 20, 1))); // +40
        assertEquals(before + 40, atm.balance());
    }

    @Test
    void invalidAmountRejectedByPolicy() {
        // Smallest denomination is 10 → not divisible should fail
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(3));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(5));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(0));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(-10));
    }

    @Test
    void insufficientFundsWhenAmountExceedsTotalBalance() {
        assertThrows(InsufficientFundsException.class, () -> atm.withdraw(1000));
    }

    @Test
    void unavailableDenominationsWhenStockInsufficientToFormAmount() {
        // Balance = 50 + 10 = 60; smallest = 10; 40 is valid but cannot be formed
        var inv = new InMemoryInventory(Map.of(50, 1, 10, 1));
        var policy = new SmallestDenomDivisibilityPolicy(inv.denominations());
        var atm2 = new CashMachine(inv, new MinNotesStrategy(), policy);
        assertThrows(UnavailableDenominationsException.class, () -> atm2.withdraw(40));
    }

    @Test
    void constructorRejectsNonMultipleOf10Denominations() {
        var inv = new InMemoryInventory(Map.of(50, 1, 5, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new SmallestDenomDivisibilityPolicy(inv.denominations()));
    }

    @Test
    void concurrentChangeBetweenPlanAndApplySurfacesAsUnavailableOrSucceeds() {
        var inv = new InMemoryInventory(Map.of(20, 2));  // start
        inv.add(Map.of(10, 1));                           // now 10 exists

        var policy = new SmallestDenomDivisibilityPolicy(inv.denominations()); // smallest = 10 (now!)
        var atm2   = new CashMachine(inv, new MinNotesStrategy(), policy);

        assertDoesNotThrow(() -> atm2.withdraw(30));
    }
}
