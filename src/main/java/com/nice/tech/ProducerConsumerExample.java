package com.nice.tech;

/**
 * Created by moham on 23/02/2016.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ProducerConsumerExample {
    ExecutorServiceThreadPool executorServiceThreadPool;
    static ArrayList<Integer> consumerdata = new ArrayList<Integer>();
    static CountDownLatch cdl = new CountDownLatch(20);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ProducerConsumerExample prodconsumer = new ProducerConsumerExample();
        prodconsumer.init();
        cdl.await();
        prodconsumer.show_list();
    }

    private void init() throws InterruptedException, ExecutionException {
        executorServiceThreadPool = new ExecutorServiceThreadPool();
        for (int i = 0; i < 10; i++) {
            executorServiceThreadPool.addThread(new Producer(i, cdl));
            executorServiceThreadPool.addThread(new Consumer(cdl));
        }
        Thread.sleep(200);
        executorServiceThreadPool.finish();
    }

    private void show_list() {
        Iterator itr = consumerdata.iterator();
        while (itr.hasNext()) {
            Object element = itr.next();
            System.out.print(element + " ");
        }
    }

    private class Producer implements Runnable {
        int data;
        private final CountDownLatch stop;

        public Producer(int datatoput, CountDownLatch stopLatch) {
            data = datatoput;
            this.stop = stopLatch;
        }

        @Override
        public void run() {
            System.out.println("Inserting Element " + data);
            try {
                executorServiceThreadPool.queue.put(data);
                Thread.sleep(100);
            } catch (InterruptedException e) {
            } finally {
                stop.countDown();
            }
        }
    }

    private class Consumer implements Runnable {
        int datatake;
        private final CountDownLatch stop;

        public Consumer(CountDownLatch stopLatch) {
            this.stop = stopLatch;
        }

        @Override
        public void run() {
            try {
                datatake = executorServiceThreadPool.queue.take();
                consumerdata.add(datatake);
                Thread.sleep(100);
            } catch (InterruptedException e) {
            } finally {
                stop.countDown();
            }
        }
    }
}