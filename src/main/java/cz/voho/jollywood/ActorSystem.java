package cz.voho.jollywood;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorSystem {
    private static final int INITIAL_ACTOR_CAPACITY = 100;
    private static Logger LOG = LoggerFactory.getLogger(ActorSystem.class);
    private final Collection<ActorHandle> actors;
    private final ExecutorService executorService;

    public ActorSystem(final int numThreads) {
        LOG.info("Creating actor system with {} thread(s).", numThreads);
        actors = Collections.synchronizedCollection(new LinkedHashSet<>(INITIAL_ACTOR_CAPACITY));
        executorService = numThreads == 1
                ? Executors.newSingleThreadExecutor()
                : Executors.newFixedThreadPool(numThreads);
    }

    public ActorHandle getNobody() {
        return null;
    }

    public ActorHandle defineActor(final ActorDefinition definition) {
        LOG.debug("Defining actor: {}", definition);
        final ActorHandle newHandle = new ActorHandle(this, definition);
        synchronized (actors) {
            actors.add(newHandle);
        }
        return newHandle;
    }

    public void undefineActor(final ActorHandle actor) {
        LOG.debug("Undefining actor {}.", actor);

        synchronized (actors) {
            if (actors.remove(actor)) {
                actors.notifyAll();
            } else {
                throw new IllegalArgumentException("Given actor does not belong here.");
            }
        }
    }

    public void broadcastMessage(final ActorHandle sender, final Object subject, final Object body) {
        broadcastMessage(new Message(sender, subject, body));
    }

    public void broadcastMessage(final Message message) {
        synchronized (actors) {
            actors.forEach(actor -> actor.sendMessage(message));
        }
    }

    public void scheduleActorProcessing(final ActorHandle actor) {
        executorService.submit(actor::processMessages);
    }

    public void closeAllActors() {
        synchronized (actors) {
            actors.forEach(ActorHandle::closeActor);
        }
    }

    public void shutdownAfterActorsClosed() throws InterruptedException {
        LOG.debug("Waiting for actors to finish...");

        synchronized (actors) {
            while (!actors.isEmpty()) {
                LOG.info("Waiting because there are {} more opened actor(s).", actors.size());
                actors.wait();
            }
        }

        LOG.debug("All actors are finished.");

        LOG.debug("Shutting down executor service...");
        executorService.shutdown();
        LOG.info("Actor system shutdown successful.");
    }
}
