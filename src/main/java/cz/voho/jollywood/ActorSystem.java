package cz.voho.jollywood;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorSystem {
    private static final int INITIAL_ACTOR_CAPACITY = 100;
    private static Logger LOG = LoggerFactory.getLogger(ActorSystem.class);
    private final Collection<ActorHandle> actors;
    private final Set<ActorHandle> processingSet;
    private final ExecutorService executorService;

    public ActorSystem(final int numThreads) {
        LOG.debug("Creating actor system with {} thread(s).", numThreads);
        actors = Collections.synchronizedCollection(new LinkedHashSet<>(INITIAL_ACTOR_CAPACITY));
        processingSet = new HashSet<>();
        executorService = numThreads == 1
                ? Executors.newSingleThreadExecutor()
                : Executors.newFixedThreadPool(numThreads);
    }

    public ActorHandle getAnonymous() {
        return new ActorHandle(this, "nobody", (a, b) -> {
        });
    }

    public ActorHandle defineActor(final String name, final ActorDefinition definition) {
        LOG.debug("Defining actor {}: {}", name, definition);
        final ActorHandle newHandle = new ActorHandle(this, name, definition);
        synchronized (actors) {
            actors.add(newHandle);
        }
        return newHandle;
    }

    public void shutdownActor(final ActorHandle actor) {
        LOG.debug("Undefining actor {}.", actor);
        final boolean lastActorRemoved;
        synchronized (actors) {
            assert actors.contains(actor);
            actors.remove(actor);
            lastActorRemoved = actors.isEmpty();
        }
        if (lastActorRemoved) {
            executorService.shutdown();
        }
    }

    public void broadcastMessage(final ActorHandle sender, final MessageContent message) {
        broadcastMessage(new Message(sender, message));
    }

    public void broadcastMessage(final Message message) {
        synchronized (actors) {
            actors.forEach(actor -> actor.sendMessage(message));
        }
    }

    public void scheduleActorProcessing(final ActorHandle actor) {
        synchronized (processingSet) {
            if (!processingSet.contains(actor)) {
                processingSet.add(actor);
                LOG.debug("Scheduling actor mailbox processing: {}", actor);
                executorService.submit(() -> {
                    actor.processMessages();

                    synchronized (processingSet) {
                        processingSet.remove(actor);
                    }
                });
            }
        }
    }

    public void shutdown() {
        LOG.debug("Shutting down all actors.");
        synchronized (actors) {
            actors.forEach(ActorHandle::close);
        }
    }
}
