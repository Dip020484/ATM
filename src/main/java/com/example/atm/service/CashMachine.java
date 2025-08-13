package com.example.atm.service;



import com.example.atm.domain.DispensePlan;
import com.example.atm.domain.Money;
import com.example.atm.errors.Errors;
import com.example.atm.ports.AmountPolicy;
import com.example.atm.ports.DispenseStrategy;
import com.example.atm.ports.Inventory;

import java.util.Map;
import java.util.Objects;

/** Public API that orchestrates validation, planning, and inventory updates. */
public final class CashMachine {
    private final Inventory inventory;
    private final DispenseStrategy strategy;
    private final AmountPolicy amountPolicy;

    public CashMachine(Inventory inventory, DispenseStrategy strategy, AmountPolicy amountPolicy) {
        this.inventory = Objects.requireNonNull(inventory);
        this.strategy = Objects.requireNonNull(strategy);
        this.amountPolicy = Objects.requireNonNull(amountPolicy);
    }

    /** Deposit notes back to inventory. */
    public void deposit(Money money) {
        Objects.requireNonNull(money, "money");
        inventory.add(money.asMap());
    }

    /** Attempt to withdraw the exact amount, returns the dispensed notes. */
    public Money withdraw(int amount) {
        amountPolicy.validate(amount);

        if (inventory.balance() < amount) {
            throw new Errors.InsufficientFundsException("ATM balance is insufficient");
        }

        var planOpt = strategy.plan(amount, inventory.snapshot());
        if (planOpt.isEmpty()) {
            throw new Errors.UnavailableDenominationsException("Cannot form " + amount + " with available notes");
        }

        DispensePlan plan = planOpt.get();
        // Apply the plan atomically at the inventory layer
        try {
            inventory.remove(plan.notes().asMap());
        } catch (IllegalStateException e) {
            // Inventory changed concurrently; surface a domain error
            throw new Errors.UnavailableDenominationsException("Inventory changed; please try again");
        }
        return plan.notes();
    }

    public int balance() { return inventory.balance(); }
    public Map<Integer, Integer> inventorySnapshot() { return inventory.snapshot(); }
}
