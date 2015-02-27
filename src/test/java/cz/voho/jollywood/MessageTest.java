package cz.voho.jollywood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessageTest {
    @Mock
    private ActorHandle senderMock;
    @Mock
    private Object subjectMock;
    @Mock
    private Object bodyMock;
    private Message message_NoSubject_NoBody;
    private Message message_Subject_NoBody;
    private Message message_NoSubject_Body;
    private Message message_Subject_Body;
    private Message message_NoSender;

    @Before
    public void prepare() {
        message_NoSubject_NoBody = new Message(senderMock, null, null);
        message_Subject_NoBody = new Message(senderMock, subjectMock, null);
        message_NoSubject_Body = new Message(senderMock, null, bodyMock);
        message_Subject_Body = new Message(senderMock, subjectMock, bodyMock);
        message_NoSender = new Message(null, null, null);

        doReturn("sender").when(senderMock).toString();
        doReturn("subject").when(subjectMock).toString();
        doReturn("body").when(bodyMock).toString();
    }

    @Test
    public void testGetters() {
        assertEquals(senderMock, message_NoSubject_NoBody.getSender());
        assertNull(message_NoSubject_NoBody.getSubject());
        assertNull(message_NoSubject_NoBody.getBody());

        assertEquals(senderMock, message_Subject_NoBody.getSender());
        assertEquals(subjectMock, message_Subject_NoBody.getSubject());
        assertNull(message_Subject_NoBody.getBody());

        assertEquals(senderMock, message_NoSubject_Body.getSender());
        assertNull(message_NoSubject_Body.getSubject());
        assertEquals(bodyMock, message_NoSubject_Body.getBody());

        assertEquals(senderMock, message_Subject_Body.getSender());
        assertEquals(subjectMock, message_Subject_Body.getSubject());
        assertEquals(bodyMock, message_Subject_Body.getBody());
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
        assertFalse(message_NoSubject_NoBody.subjectEquals(subjectMock));
        assertTrue(message_NoSubject_NoBody.subjectEquals(null));

        assertTrue(message_Subject_NoBody.subjectEquals(subjectMock));
        assertFalse(message_Subject_NoBody.subjectEquals(null));

        assertFalse(message_NoSubject_Body.subjectEquals(subjectMock));
        assertTrue(message_NoSubject_Body.subjectEquals(null));

        assertTrue(message_Subject_Body.subjectEquals(subjectMock));
        assertFalse(message_Subject_Body.subjectEquals(null));
    }

    @Test
    public void testSenderMatch() {
        assertTrue(message_NoSubject_NoBody.senderEquals(senderMock));
        assertFalse(message_NoSubject_NoBody.senderEquals(null));

        assertTrue(message_Subject_NoBody.senderEquals(senderMock));
        assertFalse(message_Subject_NoBody.senderEquals(null));

        assertTrue(message_NoSubject_Body.senderEquals(senderMock));
        assertFalse(message_NoSubject_Body.senderEquals(null));

        assertTrue(message_Subject_Body.senderEquals(senderMock));
        assertFalse(message_Subject_Body.senderEquals(null));

        assertFalse(message_NoSender.senderEquals(senderMock));
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