package cz.voho.jollywood;

/**
 * Actor definition specifies an actor reaction on different messages.
 */
@FunctionalInterface
public interface ActorDefinition {
    /**
     * Method called on each message reception.
     *
     * @param self current actor handle to perform various reaction tasks
     * @param message message received (contains sender)
     */
    void processMessage(ActorHandle self, Message message);
}
