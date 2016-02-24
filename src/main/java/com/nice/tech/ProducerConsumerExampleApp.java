package com.nice.tech;

/**
 * Created by moham on 23/02/2016.
 */

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class ProducerConsumerExampleApp implements QueueEventListener {
    private EventNotifyingDeque messages;
    private Map<String, CompanyOrderStatus> companyOrdersStatus;
    private ExecutorServiceThreadPool executorServiceThreadPool;
    private static long lifeTime = 60000l;
    static CountDownLatch cdl = new CountDownLatch(20);

    public ProducerConsumerExampleApp(EventNotifyingDeque messages, Map<String, CompanyOrderStatus> companyOrdersStatus) throws InterruptedException, ExecutionException {
        this.messages = messages;
        messages.addListener(this);
        this.companyOrdersStatus = companyOrdersStatus;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        ProducerConsumerExampleApp prodconsumer = new ProducerConsumerExampleApp(queue, companyOrdersStatus);
        prodconsumer.init();
        cdl.await();
        //  prodconsumer.show_list();
    }

    private void init() throws InterruptedException, ExecutionException {
        executorServiceThreadPool = new ExecutorServiceThreadPool();
        for (int i = 0; i < 10; i++) {
            executorServiceThreadPool.addThread(new MessageProducer(messages, lifeTime, new File("data/Trades.data"), new HashMap<String,Throwable>(),new LinkedList<String>()));
            executorServiceThreadPool.addThread(new MessageConsumer(messages));
        }
        Thread.sleep(200);
        executorServiceThreadPool.finish();
    }


    @Override
    public void messageAdded(QueueMessage queueMessage) {

    }

    @Override
    public void messageRemoved(QueueMessage queueMessage) {
        Message message = queueMessage.getMessage();
        String companyName = queueMessage.getMessage().getCompanyName();
        CompanyOrderStatus orderStatus = companyOrdersStatus.get(companyName);
        if (orderStatus == null) {
            orderStatus = new CompanyOrderStatus(companyName);
            orderStatus.setLastOrderDateTime(queueMessage.getMessage().getMessageTime());
            companyOrdersStatus.put(companyName, orderStatus);
        }
        orderStatus.getPeriodMessages().put(queueMessage.getMessageID(), message);
        if (orderStatus.getNoOfOrders() == 0) {
            orderStatus.setLastOrderEndDateTime(queueMessage.getMessageEndTime());
        }

        orderStatus.addOrder();
        if (orderStatus.getNoOfOrders() > 1 && orderStatus.getLastOrderEndDateTime().getTime() < message.getMessageTime().getTime()) {
            orderStatus.deleteNonPeriodMessages(message, lifeTime);
            orderStatus.addOrder();
            orderStatus.setLastOrderDateTime(message.getMessageTime());
            orderStatus.setLastOrderEndDateTime(queueMessage.getMessageEndTime());
        }
        orderStatus.setOrderQuantity(message.getQuantity());
        if (message.getTransactionType().equals(TransactionType.F)) {
            orderStatus.setOrderCancels(message.getQuantity());
        }

        orderStatus.checkCancels(message);
    }
}