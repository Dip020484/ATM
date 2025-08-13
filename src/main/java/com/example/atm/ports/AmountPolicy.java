package com.example.atm.ports;



/** Rule set for validating requested amounts. */
public interface AmountPolicy {
    /** Throws if invalid. */
    void validate(int amount);
}

