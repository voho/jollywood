package cz.voho.jollywood.examples;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;

public class BarrierTest {
    private static final int NUM_THREADS = 10;
    private static final int NUM_RESPONDERS = 1000000;

    @Test
    public void test() throws InterruptedException {
        final ActorSystem system = new ActorSystem(NUM_THREADS);

        for (int i = 0; i < NUM_RESPONDERS; i++) {
            system.defineActor((self, message) -> {
                if (message.subjectEquals("question")) {
                    message.getSender().sendMessage(self, "answer", null);
                    self.closeActor();
                }
            });
        }

        ActorHandle driverRef = system.defineActor((self, counter, message) -> {
            if (message.subjectEquals("ask")) {
                self.getSystem().broadcastMessage(self, "question", null);
            } else if (message.subjectEquals("answer")) {
                if (counter.incrementAndGet() == NUM_RESPONDERS) {
                    self.closeActor();
                }
            }
        }, new AtomicInteger(0));

        driverRef.sendMessage(system.getNobody(), "ask", null);
        system.shutdownAfterActorsClosed();
    }
}
