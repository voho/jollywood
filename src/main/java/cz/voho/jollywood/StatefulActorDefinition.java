package cz.voho.jollywood;

/**
 * Actor definition specifies an actor reaction on different messages.
 *
 * @author Vojtěch Hordějčuk
 */
@FunctionalInterface
public interface StatefulActorDefinition<S> {
    /**
     * Method called on each message reception.
     *
     * @param self current actor handle to perform various reaction tasks
     * @param state current actor handle state
     * @param message message received (contains sender)
     * @throws Exception any error during processing message
     */
    void processMessage(ActorHandle self, S state, Message message) throws Exception;
}
