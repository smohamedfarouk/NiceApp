package com.nice.tech;

/**
 * Created by moham on 23/02/2016.
 */
public interface Consumer extends Runnable {
     void consume(QueueMessage queueMessage);
}
