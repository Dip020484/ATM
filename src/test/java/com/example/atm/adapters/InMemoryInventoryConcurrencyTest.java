package com.example.atm.adapters;




import com.example.atm.ports.Inventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryInventoryConcurrencyTest {

    @Test
    @Timeout(10) //
    void concurrentAddRemoveCompletesAndBalanceMatchesOperations() throws Exception {
        Inventory inv = new InMemoryInventory(Map.of(10, 1000)); // start with 1,000 × £10 = £10,000

        int iterations = 50_000;
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(2);

        AtomicInteger adds = new AtomicInteger();
        AtomicInteger removes = new AtomicInteger();

        Callable<Void> adder = () -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    inv.add(Map.of(10, 1));
                    adds.incrementAndGet();
                }
                return null;
            } finally {
                latch.countDown();
            }
        };

        Callable<Void> remover = () -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    try {
                        inv.remove(Map.of(10, 1));
                        removes.incrementAndGet();
                    } catch (IllegalStateException ignored) {
                        // Expected under contention when remove outruns available notes.
                        // We ignore; the invariant we verify is final balance calculation.
                    }
                }
                return null;
            } finally {
                latch.countDown();
            }
        };

        Future<Void> f1 = pool.submit(adder);
        Future<Void> f2 = pool.submit(remover);

        // Don’t block forever
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Workers did not finish in time");

        // Surface any exceptions thrown in tasks
        f1.get(2, TimeUnit.SECONDS);
        f2.get(2, TimeUnit.SECONDS);

        pool.shutdown();
        assertTrue(pool.awaitTermination(2, TimeUnit.SECONDS), "Executor did not terminate");

        // Expected balance = (initial notes + adds - removes) * denomination
        int expected = (1000 + adds.get() - removes.get()) * 10;
        assertEquals(expected, inv.balance());
        assertTrue(inv.balance() >= 0, "Balance should never be negative");
    }
}


