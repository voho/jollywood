package cz.voho.jollywood;

import java.util.LinkedList;
import java.util.Queue;

public class Mailbox {
    private final Queue<Message> queue;

    public Mailbox() {
        this.queue = new LinkedList<>();
    }

    public Message poll() {
        Message lastMessage = null;

        synchronized (queue) {
            if (!queue.isEmpty()) {
                lastMessage = queue.poll();
            }
        }

        return lastMessage;
    }

    public void add(final Message message) {
        synchronized (queue) {
            queue.add(message);
        }
    }
}
