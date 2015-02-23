package cz.voho.jollywood;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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
        final Message message1 = createTestMessage();

        mailbox.add(message1);

        assertSame(message1, mailbox.poll());
        assertNull(mailbox.poll());
    }

    @Test
    public void testPollInCorrectOrder() throws Exception {
        final Message message1 = createTestMessage();
        final Message message2 = createTestMessage();
        final Message message3 = createTestMessage();

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
        final Message message = createTestMessage();

        for (long i = 0; i < count; i++) {
            mailbox.add(message);
        }

        for (long i = 0; i < count; i++) {
            assertSame(message, mailbox.poll());
        }

        assertNull(mailbox.poll());
    }

    private static Message createTestMessage() {
        return new Message(null, null, null);
    }
}