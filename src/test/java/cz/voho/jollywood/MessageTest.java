package cz.voho.jollywood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class MessageTest {
    private ActorHandle mockSender;
    private Object mockSubject;
    private Object mockBody;
    private Message message_NoSubject_NoBody;
    private Message message_Subject_NoBody;
    private Message message_NoSubject_Body;
    private Message message_Subject_Body;
    private Message message_NoSender;

    @Before
    public void prepare() {
        mockSender = mock(ActorHandle.class);
        mockSubject = mock(Object.class);
        mockBody = mock(Object.class);
        message_NoSubject_NoBody = new Message(mockSender, null, null);
        message_Subject_NoBody = new Message(mockSender, mockSubject, null);
        message_NoSubject_Body = new Message(mockSender, null, mockBody);
        message_Subject_Body = new Message(mockSender, mockSubject, mockBody);
        message_NoSender = new Message(null, null, null);

        doReturn("sender").when(mockSender).toString();
        doReturn("subject").when(mockSubject).toString();
        doReturn("body").when(mockBody).toString();
    }

    @Test
    public void testGetters() {
        assertEquals(mockSender, message_NoSubject_NoBody.getSender());
        assertNull(message_NoSubject_NoBody.getSubject());
        assertNull(message_NoSubject_NoBody.getBody());

        assertEquals(mockSender, message_Subject_NoBody.getSender());
        assertEquals(mockSubject, message_Subject_NoBody.getSubject());
        assertNull(message_Subject_NoBody.getBody());

        assertEquals(mockSender, message_NoSubject_Body.getSender());
        assertNull(message_NoSubject_Body.getSubject());
        assertEquals(mockBody, message_NoSubject_Body.getBody());

        assertEquals(mockSender, message_Subject_Body.getSender());
        assertEquals(mockSubject, message_Subject_Body.getSubject());
        assertEquals(mockBody, message_Subject_Body.getBody());
    }

    @Test
    public void testFlags() {
        assertFalse(message_NoSubject_NoBody.hasSubject());
        assertFalse(message_NoSubject_NoBody.hasBody());

        assertTrue(message_Subject_NoBody.hasSubject());
        assertFalse(message_Subject_NoBody.hasBody());

        assertFalse(message_NoSubject_Body.hasSubject());
        assertTrue(message_NoSubject_Body.hasBody());

        assertTrue(message_Subject_Body.hasSubject());
        assertTrue(message_Subject_Body.hasBody());
    }

    @Test
    public void testSubjectMatch() {
        assertFalse(message_NoSubject_NoBody.subjectEquals(mockSubject));
        assertTrue(message_NoSubject_NoBody.subjectEquals(null));

        assertTrue(message_Subject_NoBody.subjectEquals(mockSubject));
        assertFalse(message_Subject_NoBody.subjectEquals(null));

        assertFalse(message_NoSubject_Body.subjectEquals(mockSubject));
        assertTrue(message_NoSubject_Body.subjectEquals(null));

        assertTrue(message_Subject_Body.subjectEquals(mockSubject));
        assertFalse(message_Subject_Body.subjectEquals(null));
    }

    @Test
    public void testSenderMatch() {
        assertTrue(message_NoSubject_NoBody.senderEquals(mockSender));
        assertFalse(message_NoSubject_NoBody.senderEquals(null));

        assertTrue(message_Subject_NoBody.senderEquals(mockSender));
        assertFalse(message_Subject_NoBody.senderEquals(null));

        assertTrue(message_NoSubject_Body.senderEquals(mockSender));
        assertFalse(message_NoSubject_Body.senderEquals(null));

        assertTrue(message_Subject_Body.senderEquals(mockSender));
        assertFalse(message_Subject_Body.senderEquals(null));

        assertFalse(message_NoSender.senderEquals(mockSender));
        assertTrue(message_NoSender.senderEquals(null));
    }

    @Test
    public void testToString() {
        assertEquals("sender --> [null] null", message_NoSubject_NoBody.toString());
        assertEquals("sender --> [subject] null", message_Subject_NoBody.toString());
        assertEquals("sender --> [null] body", message_NoSubject_Body.toString());
        assertEquals("sender --> [subject] body", message_Subject_Body.toString());
        assertEquals("null --> [null] null", message_NoSender.toString());
    }
}