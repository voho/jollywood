package cz.voho.jollywood;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class MailboxTest {
    private Mailbox mailbox;

    @Before
    public void prepare() {
        mailbox = new Mailbox();
    }

    @Test
    public void testPollFromEmpty() throws Exception {
        assertNull(mailbox.poll());
    }

    @Test
    public void testPollFromSingle() throws Exception {
        final Message message1 = mock(Message.class);

        mailbox.add(message1);

        assertSame(message1, mailbox.poll());
        assertNull(mailbox.poll());
    }

    @Test
    public void testPollInCorrectOrder() throws Exception {
        final Message message1 = mock(Message.class);
        final Message message2 = mock(Message.class);
        final Message message3 = mock(Message.class);

        mailbox.add(message1);
        mailbox.add(message2);
        assertSame(message1, mailbox.poll());
        mailbox.add(message3);
        assertSame(message2, mailbox.poll());
        assertSame(message3, mailbox.poll());

        assertNull(mailbox.poll());
    }

    @Test
    public void testDoNotIgnoreSameMessages() {
        final int count = 100;
        final Message message = mock(Message.class);

        for (long i = 0; i < count; i++) {
            mailbox.add(message);
        }

        for (long i = 0; i < count; i++) {
            assertSame(message, mailbox.poll());
        }

        assertNull(mailbox.poll());
    }
}