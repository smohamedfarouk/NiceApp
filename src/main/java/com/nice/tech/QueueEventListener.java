package com.nice.tech;

/**
 * Created by moham on 22/02/2016.
 */
public interface QueueEventListener {
     void messageAdded(QueueMessage message);
     void messageRemoved(QueueMessage message);
}
