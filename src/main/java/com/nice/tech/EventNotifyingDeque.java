package com.nice.tech;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by moham on 22/02/2016.
 */

/*
    Class to notify clients when messages are added or takenFirst of the queue
 */
public class EventNotifyingDeque extends LinkedBlockingDeque<QueueMessage> {

    private List<QueueEventListener> listeners = new ArrayList();

    public EventNotifyingDeque() {
    }

    public void addListener(QueueEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void put(QueueMessage message) throws InterruptedException {
        super.put(message);
        notifyMessageAddedToListeners(message);
    }

    @Override
    public QueueMessage takeFirst() throws InterruptedException {
        QueueMessage message = super.takeFirst();
        notifyMessageRemovedToListeners(message);
        return message;
    }

    private void notifyMessageRemovedToListeners(QueueMessage message) {
        for (QueueEventListener listener : listeners) {
            listener.messageRemoved(message);
        }
    }

    private void notifyMessageAddedToListeners(QueueMessage message) {
        for (QueueEventListener listener : listeners) {
            listener.messageAdded(message);
        }
    }

}
