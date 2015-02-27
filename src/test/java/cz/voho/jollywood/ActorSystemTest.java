package cz.voho.jollywood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ActorSystemTest {
    private ActorSystem actorSystem;

    @Before
    public void initialize() {
        actorSystem = new ActorSystem(1);
    }

    @Test
    public void testGetAnonymous() throws Exception {
        ActorHandle actorHandle = actorSystem.getNobody();

        assertNull(actorHandle);
    }

    @Test
    public void testDefineStatelessActor() throws Exception {
        StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testUndefineActor() throws Exception {
        StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        ActorHandle actorHandle = actorSystem.defineActor(actorDefinitionMock);
        actorSystem.undefineActor(actorHandle);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testBroadcastMessage_direct() throws Exception {
        StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);
        Message messageMock = mock(Message.class);

        ActorHandle actor1 = actorSystem.defineActor(actorDefinitionMock);
        ActorHandle actor2 = actorSystem.defineActor(actorDefinitionMock);

        actorSystem.broadcastMessage(messageMock);
        actor1.closeActor();
        actor2.closeActor();
        actorSystem.shutdownAfterActorsClosed();

        verify(actorDefinitionMock)
                .processMessage(eq(actor1), eq(messageMock));

        verify(actorDefinitionMock)
                .processMessage(eq(actor2), eq(messageMock));

        verifyNoMoreInteractions(actorDefinitionMock, messageMock);
    }

    @Test
    public void testBroadcastMessage_indirect() throws Exception {
        StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);
        ActorHandle mockSender = mock(ActorHandle.class);
        Object mockSubject = mock(Object.class);
        Object mockBody = mock(Object.class);

        ActorHandle actor1 = actorSystem.defineActor(actorDefinitionMock);
        ActorHandle actor2 = actorSystem.defineActor(actorDefinitionMock);

        actorSystem.broadcastMessage(mockSender, mockSubject, mockBody);
        actor1.closeActor();
        actor2.closeActor();
        actorSystem.shutdownAfterActorsClosed();

        ArgumentCaptor<Message> messageCaptor1 = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<Message> messageCaptor2 = ArgumentCaptor.forClass(Message.class);

        verify(actorDefinitionMock)
                .processMessage(eq(actor1), messageCaptor1.capture());

        verify(actorDefinitionMock)
                .processMessage(eq(actor2), messageCaptor2.capture());

        Message message1 = messageCaptor1.getValue();
        Message message2 = messageCaptor2.getValue();

        assertEquals(message1, message2);
        assertEquals(mockSender, message1.getSender());
        assertEquals(mockSubject, message1.getSubject());
        assertEquals(mockBody, message1.getBody());
        assertEquals(mockSender, message2.getSender());
        assertEquals(mockSubject, message2.getSubject());
        assertEquals(mockBody, message2.getBody());

        verifyNoMoreInteractions(actorDefinitionMock, mockSender, mockSubject, mockBody);
    }

    @Test
    public void testShutdownEmptySystem() throws Exception {
        actorSystem.shutdownAfterActorsClosed();
    }

    @Test
    public void testShutdownNonEmptySystem() throws Exception {
        StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);
        actorSystem.closeAllActors();
        actorSystem.shutdownAfterActorsClosed();

        verifyNoMoreInteractions(actorDefinitionMock);
    }
}