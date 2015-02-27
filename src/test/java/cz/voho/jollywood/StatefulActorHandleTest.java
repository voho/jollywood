package cz.voho.jollywood;

import static org.mockito.Mockito.mock;

import org.junit.Before;

public class StatefulActorHandleTest {
    private ActorSystem actorSystemMock;
    private State stateMock;
    private StatefulActorDefinition<State> definitionMock;
    private ActorHandle actorHandle;

    @Before
    public void initialize() {
        actorSystemMock = mock(ActorSystem.class);
        stateMock = mock(State.class);
        actorHandle = new ActorHandle(actorSystemMock) {
            @Override
            protected void processMessage(Message message) throws Exception {
                definitionMock.processMessage(this, stateMock, message);
            }
        };
    }

    private static class State {

    }
}
