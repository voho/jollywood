package cz.voho.jollywood;

public class Mailbox {
    private final Object queueLock;
    private QueueNode head;
    private QueueNode tail;

    public Mailbox() {
        queueLock = new Object();
    }

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

    private static class QueueNode {
        private final Message payload;
        private QueueNode next;

        private QueueNode(final Message payload) {
            this.payload = payload;
        }
    }
}
