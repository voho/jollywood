package cz.voho.jollywood;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorHandle {
    private static Logger LOG = LoggerFactory.getLogger(ActorHandle.class);

    private final String name;
    private final ActorSystem system;
    private final ActorDefinition definition;
    private final Mailbox mailbox;
    private final AtomicBoolean closed;

    public ActorHandle(final ActorSystem system, final String name, final ActorDefinition definition) {
        this.system = system;
        this.name = name;
        this.definition = definition;
        this.mailbox = new Mailbox();
        this.closed = new AtomicBoolean(false);
    }

    public ActorHandle cloneActor() {
        LOG.debug("Cloning actor: {}", this);
        return system.defineActor(name + " (clone)", definition);
    }

    public ActorHandle createActor(final String name, final ActorDefinition definition) {
        LOG.debug("Creating new actor {}: {}", name, definition);
        return system.defineActor(name, definition);
    }

    public void sendMessage(final ActorHandle sender, final MessageContent messageBody) {
        sendMessage(new Message(sender, messageBody));
    }

    public void sendMessage(final Message message) {
        LOG.debug("Sending message {} to {}.", message, this);
        mailbox.add(message);
        system.scheduleActorProcessing(this);
    }

    public void broadcastMessage(final ActorHandle sender, final MessageContent messageBody) {
        broadcastMessage(new Message(sender, messageBody));
    }

    public void broadcastMessage(final Message message) {
        LOG.debug("Broadcasting message {}.", message);
        system.broadcastMessage(message);
    }

    public void processMessages() {
        while (true) {
            final Message message = mailbox.poll();

            if (message != null) {
                LOG.debug("Processing message in {}: {}", this, message);
                definition.processMessage(this, message);
            } else {
                LOG.debug("No more messages in {}.", this);

                if (closed.get()) {
                    LOG.debug("Last message processed, removing actor.");
                    system.shutdownActor(this);
                }

                break;
            }
        }
    }

    public void close() {
        LOG.debug("Closing actor: {}", this);
        closed.set(true);
        system.scheduleActorProcessing(this);
    }

    @Override
    public String toString() {
        return String.format("{%s}", name);
    }
}
