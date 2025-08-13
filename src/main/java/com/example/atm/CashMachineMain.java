package com.example.atm;



import com.example.atm.adapters.MinNotesStrategy;
import com.example.atm.adapters.InMemoryInventory;
import com.example.atm.adapters.SmallestDenomDivisibilityPolicy;
import com.example.atm.service.CashMachine;
import com.example.atm.domain.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class CashMachineMain {

    private static final Logger logger = LoggerFactory.getLogger(CashMachineMain.class);
    public static void main(String[] args) {
        Map<Integer, Integer> initial = new LinkedHashMap<>();
        initial.put(50, 2);
        initial.put(20, 3);
        initial.put(10, 5);


        var inventory = new InMemoryInventory(initial);
        var strategy  = new MinNotesStrategy();
        var policy    = new SmallestDenomDivisibilityPolicy(inventory.denominations());

        var atm = new CashMachine(inventory, strategy, policy);

        logger.info("Initial balance: " + atm.balance());

        var withdrawn = atm.withdraw(130);
        logger.info("Dispensed: {} ",withdrawn);
        logger.info("Post-balance: {} ",atm.balance());

        atm.deposit(new Money(Map.of(10, 2, 20, 1)));
        logger.info("After deposit balance:{}  ",atm.balance());
    }
}

