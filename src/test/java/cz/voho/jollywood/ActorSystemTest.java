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
        final ActorHandle actorHandle = actorSystem.getNobody();

        assertNull(actorHandle);
    }

    @Test
    public void testDefineStatelessActor() throws Exception {
        final StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testUndefineActor() throws Exception {
        final StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        final ActorHandle actorHandle = actorSystem.defineActor(actorDefinitionMock);
        actorSystem.undefineActor(actorHandle);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testBroadcastMessage_direct() throws Exception {
        final StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);
        final Message messageMock = mock(Message.class);

        final ActorHandle actor1 = actorSystem.defineActor(actorDefinitionMock);
        final ActorHandle actor2 = actorSystem.defineActor(actorDefinitionMock);

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
        final StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);
        final ActorHandle mockSender = mock(ActorHandle.class);
        final Object mockSubject = mock(Object.class);
        final Object mockBody = mock(Object.class);

        final ActorHandle actor1 = actorSystem.defineActor(actorDefinitionMock);
        final ActorHandle actor2 = actorSystem.defineActor(actorDefinitionMock);

        actorSystem.broadcastMessage(mockSender, mockSubject, mockBody);
        actor1.closeActor();
        actor2.closeActor();
        actorSystem.shutdownAfterActorsClosed();

        final ArgumentCaptor<Message> messageCaptor1 = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Message> messageCaptor2 = ArgumentCaptor.forClass(Message.class);

        verify(actorDefinitionMock)
                .processMessage(eq(actor1), messageCaptor1.capture());

        verify(actorDefinitionMock)
                .processMessage(eq(actor2), messageCaptor2.capture());

        final Message message1 = messageCaptor1.getValue();
        final Message message2 = messageCaptor2.getValue();

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
        final StatelessActorDefinition actorDefinitionMock = mock(StatelessActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);
        actorSystem.closeAllActors();
        actorSystem.shutdownAfterActorsClosed();

        verifyNoMoreInteractions(actorDefinitionMock);
    }
}