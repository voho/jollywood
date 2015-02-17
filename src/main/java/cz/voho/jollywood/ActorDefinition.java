package cz.voho.jollywood;

@FunctionalInterface
public interface ActorDefinition {
    void processMessage(ActorHandle self, Message message);
}
