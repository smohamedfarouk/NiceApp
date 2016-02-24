package com.nice.tech;

/**
 * Created by moham on 22/02/2016.
 */
public class MessageConsumer implements Consumer {
    private final EventNotifyingDeque messages;

    public MessageConsumer(EventNotifyingDeque messages) {
        this.messages = messages;
    }

    public void run() {
        while (true) {
            try {
                QueueMessage message = messages.takeFirst();
                if (MessageFactory.isMessagePoison(message.getMessage())) {
                    break;
                }
                consume(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void consume(QueueMessage queueMessage) {
        //    System.out.println("Removed: " + queueMessage);
    }
}
