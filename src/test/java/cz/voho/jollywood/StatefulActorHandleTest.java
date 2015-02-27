package cz.voho.jollywood;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatefulActorHandleTest {
    @Mock
    private ActorSystem actorSystemMock;
    @Mock
    private State stateMock;
    @Mock
    private StatefulActorDefinition<State> definitionMock;
    private ActorHandle actorHandle;

    @Before
    public void prepare() {
        actorHandle = new ActorHandle(actorSystemMock) {
            @Override
            protected void processMessage(final Message message) throws Exception {
                definitionMock.processMessage(this, stateMock, message);
            }
        };
    }

    @Test
    public void testProcessMessages() throws Exception {
        final Message messageMock = mock(Message.class);

        actorHandle.sendMessage(messageMock);
        actorHandle.processMessages();

        verify(definitionMock)
                .processMessage(eq(actorHandle), eq(stateMock), eq(messageMock));

        verify(actorSystemMock)
                .scheduleActorProcessing(eq(actorHandle));

        verifyNoMoreInteractions(actorSystemMock, definitionMock, stateMock, messageMock);
    }

    private static class State {

    }
}
