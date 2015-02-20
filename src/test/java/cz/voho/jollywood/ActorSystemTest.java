package cz.voho.jollywood;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
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
        ActorHandle actorHandle = actorSystem.getAnonymous();

        assertNull(actorHandle);
    }

    @Test
    public void testDefineActor() throws Exception {
        ActorDefinition actorDefinitionMock = mock(ActorDefinition.class);

        ActorHandle actorHandle = actorSystem.defineActor(actorDefinitionMock);

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
        //actorSystem.shutdown();

        verifyNoMoreInteractions(actorDefinitionMock);
    }
}