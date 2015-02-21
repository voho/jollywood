package cz.voho.jollywood;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;

public class ActorHandleTest {
    private ActorSystem actorSystemMock;
    private ActorDefinition actorDefinitionMock;
    private ActorHandle actorHandle;

    @Before
    public void initialize() {
        actorSystemMock = mock(ActorSystem.class);
        actorDefinitionMock = mock(ActorDefinition.class);
        actorHandle = new ActorHandle(actorSystemMock, actorDefinitionMock);
    }

    @Test
    public void testCloneActor() throws Exception {
        ActorHandle clonedActorHandle = actorHandle.cloneActor();

        assertNotEquals(clonedActorHandle, actorHandle);

        verify(actorSystemMock)
                .defineActor(eq(actorDefinitionMock));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock);
    }

    @Test
    public void testCreateActor() throws Exception {
        ActorHandle createdActorHandle = actorHandle.createActor(actorDefinitionMock);

        assertNotEquals(createdActorHandle, actorHandle);

        verify(actorSystemMock)
                .defineActor(eq(actorDefinitionMock));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock);
    }

    @Test
    public void testCloseActor() throws Exception {
        actorHandle.closeActor();

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock);
    }

    @Test
    public void testSendMessage() throws Exception {
        ActorHandle senderMock = mock(ActorHandle.class);
        MessageContent messageContent = mock(MessageContent.class);

        actorHandle.sendMessage(senderMock, messageContent);

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock, senderMock, messageContent);
    }

    @Test
    public void testProcessMessages() throws Exception {
        Message message = mock(Message.class);

        actorHandle.sendMessage(message);
        actorHandle.processMessages();

        verify(actorDefinitionMock)
                .processMessage(eq(actorHandle), eq(message));

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock, message);
    }

    @Test
    public void testProcessMessagesAfterClose() throws Exception {
        Message message = mock(Message.class);

        actorHandle.closeActor();
        actorHandle.sendMessage(message);
        actorHandle.processMessages();

        verify(actorSystemMock, times(2))
                .scheduleActorProcessing(eq(actorHandle));

        verify(actorSystemMock)
                .undefineActor(eq(actorHandle));

        verify(actorDefinitionMock)
                .processMessage(eq(actorHandle), eq(message));

        verifyNoMoreInteractions(actorSystemMock, actorDefinitionMock, message);
    }
}