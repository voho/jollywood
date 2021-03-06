package cz.voho.jollywood;

/**
 * Object representing a single message.
 *
 * @author Vojtěch Hordějčuk
 */
public class Message {
    /**
     * sender actor
     */
    private final ActorHandle sender;
    /**
     * subject (allows to decide actor reaction)
     */
    private final Object subject;
    /**
     * body (might be empty)
     */
    private final Object body;

    /**
     * Creates a new instance.
     *
     * @param sender sender actor
     * @param subject subject
     * @param body body
     */
    public Message(final ActorHandle sender, final Object subject, final Object body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
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
        return subject;
    }

    /**
     * Returns the body.
     *
     * @return body
     */
    public Object getBody() {
        return body;
    }

    /**
     * Checks if this message has any body.
     *
     * @return TRUE if the message has a body, FALSE otherwise
     */
    public boolean hasBody() {
        return body != null;
    }

    /**
     * Checks if this message has any subject.
     *
     * @return TRUE if the message has a subject, FALSE otherwise
     */
    public boolean hasSubject() {
        return subject != null;
    }

    /**
     * Checks if this message has a subject equal to the given one.
     * If both subjects are NULL, they are considered equal.
     *
     * @param otherSubject other subject
     * @return TRUE if the subjects equal, FALSE otherwise
     */
    public boolean subjectEquals(final Object otherSubject) {
        if (subject == null) {
            return otherSubject == null;
        }

        return subject.equals(otherSubject);
    }

    /**
     * Checks if this message has a sender equal to the given one.
     * If both senders are NULL, they are considered equal.
     *
     * @param otherActor other actor
     * @return TRUE if the actors equal, FALSE otherwise
     */
    public boolean senderEquals(final ActorHandle otherActor) {
        if (sender == null) {
            return otherActor == null;
        }

        return sender.equals(otherActor);
    }

    @Override
    public String toString() {
        return String.format("%s --> [%s] %s", sender, subject, body);
    }
}
