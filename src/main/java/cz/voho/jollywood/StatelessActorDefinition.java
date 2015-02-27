package cz.voho.jollywood;

@FunctionalInterface
public interface StatelessActorDefinition {
    /**
     * Method called on each message reception.
     *
     * @param self current actor handle to perform various reaction tasks
     * @param message message received (contains sender)
     * @throws Exception any error during processing message
     */
    void processMessage(ActorHandle self, Message message) throws Exception;
}
