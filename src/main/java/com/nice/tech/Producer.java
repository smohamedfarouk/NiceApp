package com.nice.tech;

/**
 * Created by moham on 23/02/2016.
 */
public interface Producer extends Runnable {
     void produce(String messageString);
}
