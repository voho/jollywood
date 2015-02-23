package cz.voho.jollywood;

/**
 * Mailbox is an unlimited queue of messages belonging to an actor.
 * Queue is implemented as a linked list so there is no hard limitation of the message count.
 *
 * @author Vojtěch Hordějčuk
 */
public class Mailbox {
    /**
     * lock for accessing queue
     */
    private final Object queueLock;
    /**
     * queue head pointer (oldest/first element)
     */
    private QueueNode head;
    /**
     * queue tail pointer (newest/last element)
     */
    private QueueNode tail;

    /**
     * Creates a new instance.
     */
    public Mailbox() {
        queueLock = new Object();
    }

    /**
     * Polls an oldest (first) message from a queue.
     *
     * @return oldest message or NULL
     */
    public Message poll() {
        Message lastMessage = null;

        synchronized (queueLock) {
            if (head != null) {
                lastMessage = head.payload;

                if (head == tail) {
                    // removing last element
                    head = null;
                    tail = null;
                } else {
                    // general case
                    head = head.next;
                }
            }
        }

        return lastMessage;
    }

    /**
     * Adds a message to the end of the queue.
     *
     * @param message message to be added
     */
    public void add(final Message message) {
        final QueueNode newNode = new QueueNode(message);

        synchronized (queueLock) {
            if (tail == null) {
                // adding first element
                head = newNode;
                tail = newNode;
            } else {
                // general case
                tail.next = newNode;
                tail = newNode;
            }
        }
    }

    /**
     * Single queue node (helper class).
     */
    private static class QueueNode {
        private final Message payload;
        private QueueNode next;

        private QueueNode(final Message payload) {
            this.payload = payload;
        }
    }
}
