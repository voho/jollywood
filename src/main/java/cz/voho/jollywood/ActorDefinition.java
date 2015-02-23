package cz.voho.jollywood;

/**
 * Actor definition specifies an actor reaction on different messages.
 *
 * @author Vojtěch Hordějčuk
 */
@FunctionalInterface
public interface ActorDefinition {
    /**
     * Method called on each message reception.
     *
     * @param self current actor handle to perform various reaction tasks
     * @param message message received (contains sender)
     * @throws Exception any error during processing message
     */
    void processMessage(ActorHandle self, Message message) throws Exception;
}
