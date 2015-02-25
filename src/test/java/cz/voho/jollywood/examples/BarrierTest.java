package cz.voho.jollywood.examples;

import org.junit.Test;

import cz.voho.jollywood.ActorDefinition;
import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;

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

        ActorHandle driverRef = system.defineActor(new ActorDefinition() {
            private int counter;

            @Override
            public void processMessage(ActorHandle self, Message message) throws Exception {
                if (message.subjectEquals("ask")) {
                    self.getSystem().broadcastMessage(self, "question", null);
                } else if (message.subjectEquals("answer")) {
                    counter++;

                    if (counter == NUM_RESPONDERS) {
                        self.closeActor();
                    }
                }
            }
        });

        driverRef.sendMessage(system.getNobody(), "ask", null);
        system.shutdownAfterActorsClosed();
    }
}
