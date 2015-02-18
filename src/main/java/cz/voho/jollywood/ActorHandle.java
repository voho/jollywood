package cz.voho.jollywood;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor handle for performing actor operations and sending messages.
 */
public class ActorHandle {
    /**
     * actor name
     */
    private final String name;
    /**
     * parent ctor system
     */
    private final ActorSystem system;
    /**
     * actor definition
     */
    private final ActorDefinition definition;
    /**
     * mailbox with messages for this actor
     */
    private final Mailbox mailbox;
    /**
     * closed flag
     */
    private final AtomicBoolean closed;

    /**
     * Creates a new instance.
     *
     * @param system parent actor system to live in
     * @param name actor name (just for easier debugging, does not have to be unique)
     * @param definition actor definition
     */
    public ActorHandle(final ActorSystem system, final String name, final ActorDefinition definition) {
        this.system = system;
        this.name = name;
        this.definition = definition;
        mailbox = new Mailbox();
        closed = new AtomicBoolean(false);
    }

    // ACTOR OPERATION
    // ===============

    public ActorHandle cloneActor() {
        return system.defineActor(name + " (clone)", definition);
    }

    public ActorHandle createActor(final String name, final ActorDefinition definition) {
        return system.defineActor(name, definition);
    }

    public void closeActor() {
        closed.set(true);
        system.scheduleActorProcessing(this);
    }

    // MESSAGE PASSING
    // ===============

    public void sendMessage(final ActorHandle sender, final MessageContent messageBody) {
        sendMessage(new Message(sender, messageBody));
    }

    public void sendMessage(final Message message) {
        mailbox.add(message);
        system.scheduleActorProcessing(this);
    }

    public void broadcastMessage(final ActorHandle sender, final MessageContent messageBody) {
        broadcastMessage(new Message(sender, messageBody));
    }

    public void broadcastMessage(final Message message) {
        system.broadcastMessage(message);
    }

    // MESSAGE PROCESSING
    // ==================

    public void processMessages() {
        while (true) {
            final Message message = mailbox.poll();

            if (message != null) {
                definition.processMessage(this, message);
                Thread.yield();
            } else {
                if (closed.get()) {
                    system.shutdownActor(this);
                }

                break;
            }
        }
    }

    // UTILITY
    // =======

    @Override
    public String toString() {
        return String.format("{%s}", name);
    }
}
