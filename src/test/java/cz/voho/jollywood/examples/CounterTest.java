package cz.voho.jollywood.examples;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.StatefulActorDefinition;
import cz.voho.jollywood.StatelessActorDefinition;

public class CounterTest {
    private static final int NUM_EXPERIMENTS = 100;
    private static final int NUM_THREADS = 20;
    private static final int COUNTER_TARGET = 1000;

    @Test
    public void test() throws InterruptedException {
        for (int i = 1; i <= NUM_EXPERIMENTS; i++) {
            System.out.println("================== EXPERIMENT " + i + " ==================");
            assertEquals(COUNTER_TARGET, t());
        }
    }

    private int t() throws InterruptedException {
        final AtomicInteger result = new AtomicInteger();
        final ActorSystem system = new ActorSystem(NUM_THREADS);

        final StatefulActorDefinition<AtomicInteger> counterDef = (self, counter, message) -> {
            if (message.subjectEquals("increment")) {
                counter.incrementAndGet();
            } else if (message.subjectEquals("total")) {
                message.getSender().sendMessage(self, "total", counter.get());
                self.closeActor();
            }
        };

        final ActorHandle counterRef = system.defineActor(counterDef, new AtomicInteger(0));

        final StatelessActorDefinition driverDef = (self, message) -> {
            if (message.subjectEquals("total")) {
                result.set((Integer) message.getBody());
                self.closeActor();
            } else if (message.subjectEquals("start")) {
                for (int i = 0; i < COUNTER_TARGET; i++) {
                    counterRef.sendMessage(self, "increment", null);
                }
                counterRef.sendMessage(self, "total", null);
            }
        };

        final ActorHandle driverRef = system.defineActor(driverDef);

        driverRef.sendMessage(system.getNobody(), "start", null);

        system.shutdownAfterActorsClosed();
        return result.get();
    }
}
