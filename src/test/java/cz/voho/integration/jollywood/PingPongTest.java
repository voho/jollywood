package cz.voho.integration.jollywood;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorDefinition;
import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;

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

        final ActorDefinition playerDef = new ActorDefinition() {
            private AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void processMessage(final ActorHandle self, final Message message) {
                if (counter.incrementAndGet() == NUM_PINGS) {
                    pings.set(counter.get());
                    self.getSystem().closeAllActors();
                } else {
                    if (message.hasSubjectEqualTo("ping")) {
                        System.out.println("PING");
                        message.getSender().sendMessage(self, "pong", null);
                    } else if (message.hasSubjectEqualTo("pong")) {
                        System.out.println("PONG");
                        message.getSender().sendMessage(self, "ping", null);
                    }
                }
            }
        };

        ActorHandle pingRef = system.defineActor(playerDef);
        ActorHandle pongRef = system.defineActor(playerDef);

        pingRef.sendMessage(pongRef, "ping", null);

        system.shutdownAfterActorsClosed();
        return pings.get();
    }
}
