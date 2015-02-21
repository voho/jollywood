package cz.voho.jollywood;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor handle for performing actor operations and sending messages.
 */
public class ActorHandle {
    /**
     * actor id
     */
    private final UUID id;
    /**
     * mailbox with messages for this actor
     */
    private final Mailbox mailbox;
    /**
     * mailbox processing lock preventing more threads from processing messages
     */
    private final Object mailboxProcessingLock;
    /**
     * parent ctor system
     */
    private final ActorSystem system;
    /**
     * actor definition
     */
    private final ActorDefinition definition;
    /**
     * closed flag
     */
    private final AtomicBoolean closed;

    /**
     * Creates a new instance.
     *
     * @param system parent actor system to live in
     * @param definition actor definition
     */
    public ActorHandle(final ActorSystem system, final ActorDefinition definition) {
        this.system = system;
        this.definition = definition;
        mailboxProcessingLock = new Object();
        id = UUID.randomUUID();
        mailbox = new Mailbox();
        closed = new AtomicBoolean(false);
    }

    // ACTOR OPERATION
    // ===============

    public ActorHandle cloneActor() {
        return system.defineActor(definition);
    }

    public ActorHandle createActor(final ActorDefinition definition) {
        return system.defineActor(definition);
    }

    public void closeActor() {
        closed.set(true);
        system.scheduleActorProcessing(this);
    }

    // MESSAGE PASSING
    // ===============

    public ActorSystem getSystem() {
        return system;
    }

    public void sendMessage(final ActorHandle sender, final MessageContent messageBody) {
        sendMessage(new Message(sender, messageBody));
    }

    public void sendMessage(final Message message) {
        System.out.println("sending message " + message);
        this.mailbox.add(message);
        this.system.scheduleActorProcessing(this);
    }

    // MESSAGE PROCESSING
    // ==================

    public void processMessages() {
        synchronized (mailboxProcessingLock) {
            while (true) {
                final Message message = mailbox.poll();

                if (message != null) {
                    try {
                        definition.processMessage(this, message);
                    } catch (Exception e) {
                        // TODO
                    }
                    Thread.yield();
                } else {
                    if (closed.get()) {
                        system.undefineActor(this);
                    }

                    break;
                }
            }
        }
    }

    // UTILITY
    // =======

    @Override
    public String toString() {
        return String.format("{%s}", id);
    }
}
