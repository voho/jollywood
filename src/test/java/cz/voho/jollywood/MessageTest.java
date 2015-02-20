package cz.voho.jollywood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class MessageTest {
    @Test
    public void testGetters() {
        final ActorHandle sender = new ActorHandle(null, null) {
            @Override
            public String toString() {
                return "sender";
            }
        };

        final Object subject = Objects.SUBJECT;
        final Object content = Objects.CONTENT;
        final Message message = new Message(sender, new MessageContent(subject, content));
        assertSame(sender, message.getSender());
        assertSame(subject, message.getSubject());
        assertSame(content, message.getBody());
        assertEquals("sender --> [SUBJECT] CONTENT", message.toString());
    }

    private enum Objects {
        SUBJECT, CONTENT
    }
}