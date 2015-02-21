package cz.voho.jollywood;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
    public void testDefineActor() throws Exception {
        ActorDefinition actorDefinitionMock = mock(ActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testUndefineActor() throws Exception {
        ActorDefinition actorDefinitionMock = mock(ActorDefinition.class);

        ActorHandle actorHandle = actorSystem.defineActor(actorDefinitionMock);
        actorSystem.undefineActor(actorHandle);

        verifyNoMoreInteractions(actorDefinitionMock);
    }

    @Test
    public void testBroadcastMessage() throws Exception {
        ActorDefinition actorDefinitionMock = mock(ActorDefinition.class);
        Message messageMock = mock(Message.class);

        ActorHandle actor1 = actorSystem.defineActor(actorDefinitionMock);
        ActorHandle actor2 = actorSystem.defineActor(actorDefinitionMock);
        ActorHandle actor3 = actorSystem.defineActor(actorDefinitionMock);

        actorSystem.broadcastMessage(messageMock);
        actor1.closeActor();
        actor2.closeActor();
        actor3.closeActor();
        actorSystem.shutdown();

        verify(actorDefinitionMock)
                .processMessage(eq(actor1), eq(messageMock));

        verify(actorDefinitionMock)
                .processMessage(eq(actor2), eq(messageMock));

        verify(actorDefinitionMock)
                .processMessage(eq(actor3), eq(messageMock));

        verifyNoMoreInteractions(actorDefinitionMock, messageMock);
    }


    @Test
    public void testScheduleActorProcessing() throws Exception {

    }


    @Test
    public void testShutdownEmptySystem() throws Exception {
        actorSystem.shutdown();
    }

    @Test
    @Ignore
    public void testShutdownNonEmptySystem() throws Exception {
        ActorDefinition actorDefinitionMock = mock(ActorDefinition.class);

        actorSystem.defineActor(actorDefinitionMock);
        actorSystem.shutdown();

        verifyNoMoreInteractions(actorDefinitionMock);
    }
}