package com.example.atm.domain;




import java.util.Objects;

/** Immutable result of a dispense strategy. */
public final class DispensePlan {
    private final Money notes;

    public DispensePlan(Money notes) {
        this.notes = Objects.requireNonNull(notes, "notes");
    }

    public Money notes() { return notes; }
    public int amount() { return notes.total(); }

    @Override public String toString() { return "DispensePlan{amount=" + amount() + ", notes=" + notes + '}'; }
}

