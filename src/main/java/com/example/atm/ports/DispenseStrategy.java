package com.example.atm.ports;



import com.example.atm.domain.DispensePlan;

import java.util.Map;
import java.util.Optional;

/** Algorithm to produce a plan for dispensing an amount with limited inventory. */
public interface DispenseStrategy {
    Optional<DispensePlan> plan(int amount, Map<Integer, Integer> inventory);
}

