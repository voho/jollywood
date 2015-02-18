package cz.voho.jollywood;

/**
 * Object representing a single message.
 */
public class Message {
    /**
     * sender actor
     */
    private final ActorHandle sender;
    /**
     * content
     */
    private final MessageContent content;

    /**
     * Creates a new instance.
     *
     * @param sender sender actor
     * @param content content
     */
    public Message(final ActorHandle sender, final MessageContent content) {
        this.sender = sender;
        this.content = content;
    }

    /**
     * Returns the sender actor.
     *
     * @return sender actor
     */
    public ActorHandle getSender() {
        return sender;
    }

    /**
     * Returns the subject.
     *
     * @return subject
     */
    public Object getSubject() {
        return content.getSubject();
    }

    /**
     * Returns the body.
     *
     * @return body
     */
    public Object getBody() {
        return content.getBody();
    }

    @Override
    public String toString() {
        return String.format("%s --> %s", sender, content);
    }
}
