package cz.voho.jollywood.examples;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorDefinition;
import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;

public class RockPaperScissorsTest {
    private static final int NUM_THREADS = 10;
    private static final int NUM_GAMES = 100;
    private static final Random RANDOM = new Random();

    @Test
    public void test() throws InterruptedException {
        final ActorSystem system = new ActorSystem(NUM_THREADS);
        final AtomicInteger games = new AtomicInteger(0);
        final AtomicInteger draws = new AtomicInteger(0);
        final AtomicInteger winsOfPlayer1 = new AtomicInteger(0);
        final AtomicInteger winsOfPlayer2 = new AtomicInteger(0);

        ActorDefinition player = new ActorDefinition() {
            @Override
            public void processMessage(ActorHandle self, Message message) throws Exception {
                if (message.hasSubjectEqualTo("choose")) {
                    message.getSender().sendMessage(self, "choice", Choice.random());
                }
            }
        };

        ActorHandle player1Ref = system.defineActor(player);
        ActorHandle player2Ref = system.defineActor(player);

        ActorDefinition arbiter = new ActorDefinition() {
            private Choice player1Choice;
            private Choice player2Choice;

            @Override
            public void processMessage(ActorHandle self, Message message) throws Exception {
                if (message.hasSubjectEqualTo("play")) {
                    player1Ref.sendMessage(self, "choose", null);
                    player2Ref.sendMessage(self, "choose", null);
                } else if (message.hasSubjectEqualTo("choice")) {
                    if (message.getSender() == player1Ref) {
                        player1Choice = (Choice) message.getBody();
                    } else if (message.getSender() == player2Ref) {
                        player2Choice = (Choice) message.getBody();
                    }

                    if (player1Choice != null && player2Choice != null) {
                        if (player1Choice == player2Choice) {
                            draws.incrementAndGet();
                        } else if (player1Choice.beats(player2Choice)) {
                            winsOfPlayer1.incrementAndGet();
                        } else {
                            winsOfPlayer2.incrementAndGet();
                        }

                        if (games.incrementAndGet() < NUM_GAMES) {
                            player1Choice = null;
                            player2Choice = null;
                            self.sendMessage(self, "play", null);
                        } else {
                            system.closeAllActors();
                        }
                    }
                }
            }
        };

        ActorHandle arbiterRef = system.defineActor(arbiter);
        arbiterRef.sendMessage(system.getNobody(), "play", null);
        system.shutdownAfterActorsClosed();

        assertEquals(games.get(), winsOfPlayer1.get() + winsOfPlayer2.get() + draws.get());
    }

    private static enum Choice {
        ROCK {
            @Override
            boolean beats(Choice other) {
                return other == SCISSORS;
            }
        },
        PAPER {
            @Override
            boolean beats(Choice other) {
                return other == ROCK;
            }
        },
        SCISSORS {
            @Override
            boolean beats(Choice other) {
                return other == PAPER;
            }
        };

        abstract boolean beats(Choice other);

        public static Choice random() {
            return Choice.values()[RANDOM.nextInt(Choice.values().length)];
        }
    }
}
