package cz.voho.jollywood.examples;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cz.voho.jollywood.ActorHandle;
import cz.voho.jollywood.ActorSystem;
import cz.voho.jollywood.Message;
import cz.voho.jollywood.StatefulActorDefinition;
import cz.voho.jollywood.StatelessActorDefinition;

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

        StatelessActorDefinition player = (self, message) -> {
            if (message.subjectEquals("choose")) {
                message.getSender().sendMessage(self, "choice", Choice.random());
            }
        };

        ActorHandle player1Ref = system.defineActor(player);
        ActorHandle player2Ref = system.defineActor(player);

        StatefulActorDefinition arbiter = new StatefulActorDefinition<Choices>() {
            @Override
            public void processMessage(ActorHandle self, Choices state, Message message) throws Exception {
                if (message.subjectEquals("play")) {
                    player1Ref.sendMessage(self, "choose", null);
                    player2Ref.sendMessage(self, "choose", null);
                } else if (message.subjectEquals("choice")) {
                    if (message.senderEquals(player1Ref)) {
                        state.choiceOfPlayer1 = (Choice) message.getBody();
                    } else if (message.senderEquals(player2Ref)) {
                        state.choiceOfPlayer2 = (Choice) message.getBody();
                    } else {
                        throw new IllegalStateException("Unknown player.");
                    }

                    if (state.choiceOfPlayer1 != null && state.choiceOfPlayer2 != null) {
                        if (state.choiceOfPlayer1.beats(state.choiceOfPlayer2)) {
                            winsOfPlayer1.incrementAndGet();
                        } else if (state.choiceOfPlayer2.beats(state.choiceOfPlayer1)) {
                            winsOfPlayer2.incrementAndGet();
                        } else {
                            draws.incrementAndGet();
                        }

                        if (games.incrementAndGet() < NUM_GAMES) {
                            state.choiceOfPlayer1 = null;
                            state.choiceOfPlayer2 = null;
                            self.sendMessage(self, "play", null);
                        } else {
                            system.closeAllActors();
                        }
                    }
                }
            }
        };

        ActorHandle arbiterRef = system.defineActor(arbiter, new Choices());
        arbiterRef.sendMessage(system.getNobody(), "play", null);
        system.shutdownAfterActorsClosed();

        assertEquals(games.get(), winsOfPlayer1.get() + winsOfPlayer2.get() + draws.get());
    }

    private enum Choice {
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

    private static class Choices {
        Choice choiceOfPlayer1;
        Choice choiceOfPlayer2;
    }
}
