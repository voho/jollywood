package cz.voho.integration.jollywood;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorDefinition;
import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;
import cz.voho.jollywood.MessageContent;

public class PingPongTest {
    private static final int NUM_EXPERIMENTS = 10;
    private static final int NUM_THREADS = 20;
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
            private int counter = 0;

            @Override
            public void processMessage(final ActorHandle self, final Message message) {
                if (message.getSubject().equals("ping")) {
                    System.out.println("PING");
                    counter++;
                    MessageContent msg = new MessageContent("pong", null);
                    message.getSender().sendMessage(self, msg);
                } else if (message.getSubject().equals("pong")) {
                    System.out.println("PONG");
                    counter++;
                    MessageContent msg = new MessageContent("ping", null);
                    message.getSender().sendMessage(self, msg);
                }

                if (counter == NUM_PINGS) {
                    pings.set(counter);
                    self.closeActor();
                    synchronized (pings) {
                        pings.notifyAll();
                    }
                }
            }
        };

        ActorHandle pingDef = system.defineActor(playerDef);
        ActorHandle pongDef = system.defineActor(playerDef);

        pingDef.sendMessage(pongDef, new MessageContent("ping", null));

       // system.shutdown();
        synchronized (pings) {
            pings.wait();
        }
        return pings.get();
    }


}
