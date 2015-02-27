package cz.voho.jollywood.examples;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.StatefulActorDefinition;

public class PingPongTest {
    private static final int NUM_EXPERIMENTS = 10;
    private static final int NUM_THREADS = 4;
    private static final int NUM_PINGS = 10;

    @Test
    public void test() throws InterruptedException {
        for (int i = 1; i <= NUM_EXPERIMENTS; i++) {
            System.out.println("================== EXPERIMENT " + i + " ==================");
            assertEquals(NUM_PINGS, t());
        }
    }

    private int t() throws InterruptedException {
        final ActorSystem system = new ActorSystem(NUM_THREADS);
        final AtomicInteger pings = new AtomicInteger();

        final StatefulActorDefinition<AtomicInteger> playerDef = (self, counter, message) -> {
            if (counter.incrementAndGet() == NUM_PINGS) {
                pings.set(counter.get());
                self.getSystem().closeAllActors();
            } else {
                if (message.subjectEquals("ping")) {
                    System.out.println("PING");
                    message.getSender().sendMessage(self, "pong", null);
                } else if (message.subjectEquals("pong")) {
                    System.out.println("PONG");
                    message.getSender().sendMessage(self, "ping", null);
                }
            }
        };

        final ActorHandle pingRef = system.defineActor(playerDef, new AtomicInteger(0));
        final ActorHandle pongRef = system.defineActor(playerDef, new AtomicInteger(0));

        pingRef.sendMessage(pongRef, "ping", null);

        system.shutdownAfterActorsClosed();
        return pings.get();
    }
}
