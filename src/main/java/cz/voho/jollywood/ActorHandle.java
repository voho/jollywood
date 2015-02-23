package cz.voho.jollywood;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor handle for performing actor operations and sending messages.
 */
public class ActorHandle {
    private static Logger LOG = LoggerFactory.getLogger(ActorHandle.class);

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
    private final AtomicBoolean closeOnNoMoreMessages;

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
        closeOnNoMoreMessages = new AtomicBoolean(false);
    }

    // ACTOR OPERATION
    // ===============

    public void closeActor() {
        closeOnNoMoreMessages.set(true);
        system.scheduleActorProcessing(this);
    }

    // MESSAGE PASSING
    // ===============

    public ActorSystem getSystem() {
        return system;
    }

    public void sendMessage(final ActorHandle sender, final Object subject, final Object body) {
        sendMessage(new Message(sender, subject, body));
    }

    public void sendMessage(final Message message) {
        mailbox.add(message);
        system.scheduleActorProcessing(this);
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
                        LOG.error("Error while processing message: " + message, e);
                    } finally {
                        Thread.yield();
                    }
                } else {
                    if (closeOnNoMoreMessages.get()) {
                        LOG.debug("Last message processed - closing actor.");
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
