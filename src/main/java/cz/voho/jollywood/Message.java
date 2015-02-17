package cz.voho.jollywood;

public class Message {
    private final ActorHandle sender;
    private final MessageContent content;

    public Message(final ActorHandle sender, final MessageContent content) {
        this.sender = sender;
        this.content = content;
    }

    public ActorHandle getSender() {
        return sender;
    }

    public Object getSubject() {
        return content.getSubject();
    }

    public Object getBody() {
        return content.getBody();
    }

    @Override
    public String toString() {
        return String.format("%s --> %s", sender, content);
    }
}
