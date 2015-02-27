package cz.voho.jollywood;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;

public class StatelessActorHandleTest {
    private ActorSystem actorSystemMock;
    private StatelessActorDefinition definition;
    private ActorHandle actorHandle;

    @Before
    public void initialize() {
        actorSystemMock = mock(ActorSystem.class);
        definition = mock(StatelessActorDefinition.class);
        actorHandle = new ActorHandle(actorSystemMock) {
            @Override
            protected void processMessage(Message message) throws Exception {
                definition.processMessage(this, message);
            }
        };
    }

    @Test
    public void testCreateActor() throws Exception {
        ActorHandle createdActorHandle = actorHandle.getSystem().defineActor(definition);

        assertNotEquals(createdActorHandle, actorHandle);

        verify(actorSystemMock)
                .defineActor(eq(definition));

        verifyNoMoreInteractions(actorSystemMock, definition);
    }

    @Test
    public void testCloseActor() throws Exception {
        actorHandle.closeActor();

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, definition);
    }

    @Test
    public void testSendMessage() throws Exception {
        Message messageMock = mock(Message.class);

        actorHandle.sendMessage(messageMock);

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, definition, messageMock);
    }

    @Test
    public void testProcessMessages() throws Exception {
        Message messageMock = mock(Message.class);

        actorHandle.sendMessage(messageMock);
        actorHandle.processMessages();

        verify(definition)
                .processMessage(eq(actorHandle), eq(messageMock));

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, definition, messageMock);
    }

    @Test
    public void testProcessMessagesAfterClose() throws Exception {
        Message messageMock = mock(Message.class);

        actorHandle.closeActor();
        actorHandle.sendMessage(messageMock);
        actorHandle.processMessages();

        verify(actorSystemMock, times(2))
                .scheduleActorProcessing(eq(actorHandle));

        verify(actorSystemMock)
                .undefineActor(eq(actorHandle));

        verify(definition)
                .processMessage(eq(actorHandle), eq(messageMock));

        verifyNoMoreInteractions(actorSystemMock, definition, messageMock);
    }

    @Test
    public void testProcessMessagesWithError() throws Exception {
        doThrow(new IllegalStateException("testing error"))
                .when(definition)
                .processMessage(any(ActorHandle.class), any(Message.class));

        Message messageMock = mock(Message.class);

        actorHandle.sendMessage(messageMock);
        actorHandle.processMessages();

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verify(definition)
                .processMessage(eq(actorHandle), eq(messageMock));

        verifyNoMoreInteractions(actorSystemMock, definition, messageMock);
    }

    @Test
    public void testToString() {
        assertTrue(actorHandle.toString().matches("\\{[a-zA-Z0-9\\-]{36}\\}"));
    }
}