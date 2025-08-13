package com.example.atm.errors;



public final class Errors {
    private Errors() {}

    public static class InvalidAmountException extends RuntimeException {
        public InvalidAmountException(String msg) { super(msg); }
    }
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String msg) { super(msg); }
    }
    public static class UnavailableDenominationsException extends RuntimeException {
        public UnavailableDenominationsException(String msg) { super(msg); }
    }
}

