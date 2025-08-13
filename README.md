ATM / CashMachine – Simple, Clean, Testable
A small Java project that lets an ATM give cash using the fewest notes while respecting limited stock (how many notes are left).

Built with clean design so it’s easy to change and test.

Uses interfaces so you can swap parts without touching the rest.

What this project shows
Correct results: either gives the exact amount or clearly says why it can’t.

Thread-safe inventory: safe when many actions happen at once.

Easy to test: unit tests cover the most important parts.

Easy to change: swap the algorithm, rules, or storage.

How it works (in 3 steps)
Validate the amount
Check the request is positive and a multiple of the smallest note (e.g., 5).

Plan the notes
Pick notes from largest to smallest, but never take more than we have.

Apply the plan
Remove those notes from inventory in a thread-safe way.

If something goes wrong, it throws a clear error:

InvalidAmountException – bad amount (≤ 0 or not a multiple of the smallest note)

InsufficientFundsException – ATM doesn’t have enough total money

UnavailableDenominationsException – total money is enough, but we can’t make that exact amount with current notes (or stock changed mid-way)

SOLID in simple words
S – Single Responsibility: each class has one job.

O – Open/Closed: add new behavior by adding new classes, not by changing old ones.

L – Liskov: any implementation of an interface can be swapped in and still work.

I – Interface Segregation: small, focused interfaces.

D – Dependency Inversion: high-level code depends on interfaces, not concrete classes.


Check the com.example.atm.service.CashMachine.java class for the money withdraw function.


