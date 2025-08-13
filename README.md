# ATM / CashMachine — SOLID, Simple, Testable

> Java 17 project that lets an ATM **dispense cash with the fewest notes** while respecting **limited stock**.  
> Built with **SOLID**  principles so you can swap algorithms, rules, or storage without breaking the app.


---
## Key Features

- **Minimum notes**: Chooses notes that sum to the amount using as few notes as possible (strategy is swappable).
- **Respects stock limits**: Only dispenses what the inventory actually has.
- **Amount rules (policy)**: Amount must be positive and divisible by the **smallest available denomination**. Optional check for **×10 denominations** (10, 20, 50, …).
- **Thread-safe inventory** (for the in‑memory adapter).
- **Clear domain exceptions** for predictable error handling.
- **Easy to test**: Business logic sits behind small interfaces.

---
## SOLID (simple)

**S — Single Responsibility**: One class = one job.
- `CashMachine` only coordinates withdraw/deposit.
- `MinNotesStrategy` only decides which notes to use.
- `InMemoryInventory` only stores & updates note counts.
- `AmountPolicy` (e.g., `SmallestDenomDivisibilityPolicy`) only checks amounts.
- `Money` only represents notes and totals.

**O — Open/Closed**: Add new things without changing old code.
- New algorithm? Implement `DispenseStrategy`.
- New rule? Implement `AmountPolicy`.
- New storage? Implement `Inventory`.

**L — Liskov Substitution**: Any implementation can replace the interface and still work.
- Strategies return a correct plan (or throw expected exceptions).
- Inventories behave the same for add/remove/balance.
- Policies either validate or throw `InvalidAmountException`.

**I — Interface Segregation**: Small, focused interfaces.
- `DispenseStrategy#plan(...)`, `AmountPolicy#validate(...)`, `Inventory` methods only for inventory.

**D — Dependency Inversion**: Depend on interfaces, not concrete classes.
- `CashMachine` takes `Inventory`, `DispenseStrategy`, `AmountPolicy` via constructor.

---

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




