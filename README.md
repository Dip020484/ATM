# ATM / CashMachine — SOLID, Simple, Testable

> Java 17 project that lets an ATM **dispense cash with the fewest notes** while respecting **limited stock**.  
> Built with **SOLID**  principles so you can swap algorithms, rules, or storage without breaking the app.


---

## TL;DR

- **What it does:** Withdraws an exact amount using **minimum notes** given current stock; or fails with a clear reason.  
- **Why it’s interesting:** Clean separation of concerns → easy to test and extend (algorithms, policies, storage).  
- **How to run:** `mvn test` then `java -cp target/… com.example.atm.CashMachineMain` (see details below).

---



## Key goals

- **Correctness**: exact amount or a clear error; uses the **fewest notes** possible with current inventory.  
- **Thread-safety**: safe updates under concurrent access.  
- **SOLID**:
- SOLID in simple words S – Single Responsibility: each class has one job.

O – Open/Closed: add new behavior by adding new classes, not by changing old ones.

L – Liskov: any implementation of an interface can be swapped in and still work.

I – Interface Segregation: small, focused interfaces.

D – Dependency Inversion: high-level code depends on interfaces, not concrete classes.



**Tests (JUnit 4)**

```
src/test/java/com/example/atm
├─ domain/MoneyTest.java
├─ domain/MinNotesStrategyTest.java
├─ adapters/InMemoryInventoryTest.java
└─ service/CashMachineTest.java
```
---

## Build & run

**Requirements:** Java 17+

### Maven

```bash
mvn -q test
mvn -q package
java -cp target/atm-1.0-SNAPSHOT.jar com.example.atm.CashMachineMain
```



```

---

## How it works

1. **Validate amount** (`AmountPolicy`)  
   Default rule: amount > 0 and multiple of the **smallest available** note.

2. **Plan notes** (`DispenseStrategy`)  
   Greedy algorithm: try larger notes first, but never exceed available counts.

3. **Apply plan** (`Inventory`)  
   Remove the chosen notes atomically. If stock changed, map to a clean domain error.

---

## Errors & rules

| Error                                   | When it happens                                                                                  |
|-----------------------------------------|---------------------------------------------------------------------------------------------------|
| `InvalidAmountException`                | Amount ≤ 0, or not a multiple of the **current** smallest note (as seen when the policy was made) |
| `InsufficientFundsException`            | Amount > total money in the ATM                                                                  |
| `UnavailableDenominationsException`     | Amount ≤ balance but can’t be formed with current notes, **or** a race causes `remove()` to fail |
| `IllegalArgumentException`              | All denominations must be positive multiples of 10 |



---

## Testing

- **Domain** — totals, validation, immutability (`MoneyTest`)  
- **Strategy** — plan success/failure and guards (`MinNotesStrategyTest`)  
- **Inventory** — add/remove/snapshot/balance (`InMemoryInventoryTest`)  
- **Concurrency** — parallel add/remove with timeouts (`InMemoryInventoryConcurrencyTest`)  
- **Façade** — happy path + all error paths (`CashMachineTest`)

> Common gotcha: Expecting `UnavailableDenominationsException` but getting `InsufficientFundsException`.  
> Ensure the **amount ≤ balance** yet **not buildable** with current notes (e.g., {50×1, 5×2} and ask for 40).

---




