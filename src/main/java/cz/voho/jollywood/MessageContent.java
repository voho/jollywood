package cz.voho.jollywood;

public class MessageContent {
    private Object subject;
    private Object body;

    public MessageContent(final Object subject, final Object body) {
        this.subject = subject;
        this.body = body;
    }

    public Object getSubject() {
        return subject;
    }

    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", subject, body);
    }
}
