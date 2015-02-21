package cz.voho.integration.jollywood;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorDefinition;
import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;
import cz.voho.jollywood.MessageContent;

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

        final ActorDefinition counterDef = new ActorDefinition() {
            private int counter = 0;

            @Override
            public void processMessage(final ActorHandle self, final Message message) {
                if (message.getSubject().equals("increment")) {
                    counter++;
                } else if (message.getSubject().equals("total")) {
                    message.getSender().sendMessage(self, new MessageContent("total", counter));
                    self.closeActor();
                }
            }
        };

        final ActorHandle counterRef = system.defineActor(counterDef);

        final ActorDefinition driverDef = (self, message) -> {
            if (message.getSubject().equals("total")) {
                result.set((Integer) message.getBody());
                self.closeActor();
            } else if (message.getSubject().equals("start")) {
                for (int i = 0; i < COUNTER_TARGET; i++) {
                    counterRef.sendMessage(self, new MessageContent("increment", null));
                }
                counterRef.sendMessage(self, new MessageContent("total", null));
            }
        };

        final ActorHandle driverRef = system.defineActor(driverDef);

        driverRef.sendMessage(system.getNobody(), new MessageContent("start", null));

        system.shutdown();
        return result.get();
    }
}
