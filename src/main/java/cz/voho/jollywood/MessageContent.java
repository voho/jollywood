package cz.voho.jollywood;

/**
 * Content of a message.
 */
public class MessageContent {
    /**
     * message subject
     */
    private Object subject;
    /**
     * message body
     */
    private Object body;

    /**
     * Creates a new instance.
     *
     * @param subject subject, usually used to distinguish the message type
     * @param body body, usually parameters of a message
     */
    public MessageContent(final Object subject, final Object body) {
        this.subject = subject;
        this.body = body;
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

    @Override
    public String toString() {
        return String.format("[%s] %s", subject, body);
    }
}
