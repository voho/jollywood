package cz.voho.jollywood;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor system is responsible for holding and scheduling actors.
 *
 * @author Vojtěch Hordějčuk
 */
public class ActorSystem {
    /**
     * initial capacity of the actor set (because the default value is too low)
     */
    private static final int INITIAL_ACTOR_CAPACITY = 100;
    /**
     * logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ActorSystem.class);
    /**
     * collection of all actors present in this system
     */
    private final Collection<ActorHandle> actors;
    /**
     * executor service for scheduling tasks
     */
    private final ExecutorService executorService;

    /**
     * Creates a new instance.
     *
     * @param numThreads number of threads to process messages
     */
    public ActorSystem(final int numThreads) {
        LOG.info("Creating actor system with {} thread(s).", numThreads);
        actors = Collections.synchronizedCollection(new LinkedHashSet<>(INITIAL_ACTOR_CAPACITY));
        executorService = numThreads == 1
                ? Executors.newSingleThreadExecutor()
                : Executors.newFixedThreadPool(numThreads);
    }

    /**
     * Returns an anonymous actor handle for sending message from "nowhere".
     *
     * @return anonymous actor handle
     */
    public ActorHandle getNobody() {
        return null;
    }

    /**
     * Defines a new actor and returns its handle.
     *
     * @param definition actor behavior definition
     * @return newly defined actor handle
     */
    public ActorHandle defineActor(final ActorDefinition definition) {
        LOG.debug("Defining actor: {}", definition);
        final ActorHandle newHandle = new ActorHandle(this, definition);

        synchronized (actors) {
            actors.add(newHandle);
        }

        return newHandle;
    }

    /**
     * Removes an actor. Actor must be present here.
     *
     * @param actor actor to be removed
     */
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

    /**
     * Broadcasts message to all actors in this system.
     *
     * @param sender sender actor
     * @param subject message subject
     * @param body message body
     */
    public void broadcastMessage(final ActorHandle sender, final Object subject, final Object body) {
        broadcastMessage(new Message(sender, subject, body));
    }

    /**
     * Broadcasts message to all actors in this system.
     *
     * @param message message
     */
    public void broadcastMessage(final Message message) {
        synchronized (actors) {
            actors.forEach(actor -> actor.sendMessage(message));
        }
    }

    /**
     * Schedules actor message processing at some point in the future.
     *
     * @param actor actor to be scheduled
     */
    public void scheduleActorProcessing(final ActorHandle actor) {
        executorService.submit(actor::processMessages);
    }

    /**
     * Closes all actors in this system.
     */
    public void closeAllActors() {
        synchronized (actors) {
            actors.forEach(ActorHandle::closeActor);
        }
    }

    /**
     * Waits until all actors are closed and then shuts the system down.
     *
     * @throws InterruptedException if the waiting is interrupted
     */
    public void shutdownAfterActorsClosed() throws InterruptedException {
        LOG.debug("Waiting for actors to finish...");

        synchronized (actors) {
            while (!actors.isEmpty()) {
                LOG.info("Waiting because there are {} more opened actor(s).", actors.size());
                actors.wait();
            }
        }

        LOG.debug("All actors are finished.");

        executorService.shutdown();

        LOG.info("Actor system shutdown successful.");
    }
}
