package com.example.atm;



import com.example.atm.adapters.GreedyMinNotesStrategy;
import com.example.atm.adapters.InMemoryInventory;
import com.example.atm.adapters.SmallestDenomDivisibilityPolicy;
import com.example.atm.service.CashMachine;
import com.example.atm.domain.Money;

import java.util.LinkedHashMap;
import java.util.Map;

public class CashMachineMain {
    public static void main(String[] args) {
        Map<Integer, Integer> initial = new LinkedHashMap<>();
        initial.put(50, 2);
        initial.put(20, 3);
        initial.put(10, 5);
        initial.put(5,  10);

        var inventory = new InMemoryInventory(initial);
        var strategy  = new GreedyMinNotesStrategy();
        var policy    = new SmallestDenomDivisibilityPolicy(inventory.denominations());

        var atm = new CashMachine(inventory, strategy, policy);

        System.out.println("Initial balance: " + atm.balance());

        var withdrawn = atm.withdraw(130);
        System.out.println("Dispensed: " + withdrawn);
        System.out.println("Post-balance: " + atm.balance());

        atm.deposit(new Money(Map.of(10, 2, 5, 1)));
        System.out.println("After deposit balance: " + atm.balance());
    }
}

