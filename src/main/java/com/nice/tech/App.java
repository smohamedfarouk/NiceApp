package com.nice.tech;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Requirement: Application to create a report of companies performing excessive cancelling.
 * Write a program to process the data file and print a line to the console when it detects that a company is engaged in excessive cancelling.
 * The console line should look like this (italics are not expected):
 * Assumption: cancellation only within the same period
 * Scenario: when within the same period we have more cancellations than order
 * Explain Poison pill pattern for termination
 * Explain Use of UUID
 * Explain use of Factory Pattern
 * Explain use of Event Notifying pattern
 * Explain use of Executors and ThreadPool for aysnchronous execution
 * Explain use of Consumer and producer pattern
 * Explain exception design -     Map<String, Exception> fileExceptions = new HashMap();
 * Explain Thread count and design
 */
public class App implements QueueEventListener {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long period = 60000l;
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(new File("data/Trades.data"), new HashMap<String, Throwable>(),new LinkedList<String>());
    }

    private EventNotifyingDeque messages;
    private Map<String, CompanyOrderStatus> companyOrdersStatus;

    public Map<String, CompanyOrderStatus> getCompanyOrdersStatus() {
        return companyOrdersStatus;
    }

    private final long period;

    //Concurrency
    private ExecutorServiceThreadPool executorServiceThreadPool;

    public App(EventNotifyingDeque messages, Map<String, CompanyOrderStatus> companyOrdersStatus, long period) throws InterruptedException, ExecutionException {
        this.messages = messages;
        this.companyOrdersStatus = companyOrdersStatus;
        this.period = period;
        messages.addListener(this);
    }

    //Initialize the producer and consumer
    public void start(File file, Map<String, Throwable> fileExceptions, List<String> excessiveCancellationReport) {
        executorServiceThreadPool = new ExecutorServiceThreadPool();
        MessageProducer producer = new MessageProducer(messages, period, file, fileExceptions,excessiveCancellationReport);
        MessageConsumer consumer = new MessageConsumer(messages);
        executorServiceThreadPool.addThread(producer);
        executorServiceThreadPool.addThread(consumer);
        executorServiceThreadPool.finish();
    }


    public void messageAdded(QueueMessage queueMessage) {
        // System.out.println("Added: " + queueMessage);
    }

    public void messageRemoved(QueueMessage queueMessage) {
        //   System.out.println("Removed: " + queueMessage);
        if(! queueMessage.getMessage().getCompanyName().equals(MessageFactory.NO_CLIENT)){
            processMessage(queueMessage);
        }
    }

    private void processMessage(QueueMessage queueMessage) {
        Message newMessage = queueMessage.getMessage();
        String companyName = queueMessage.getMessage().getCompanyName();
        CompanyOrderStatus orderStatus = companyOrdersStatus.get(companyName);
        if (orderStatus == null) {
            orderStatus = new CompanyOrderStatus(companyName);
            orderStatus.setLastOrderDateTime(queueMessage.getMessage().getMessageTime());
            orderStatus.setLastOrderEndDateTime(queueMessage.getMessageEndTime());
            orderStatus.getPeriodMessages().put(queueMessage.getMessageID(), newMessage);
            companyOrdersStatus.put(companyName, orderStatus);
        }
        if (orderStatus.getLastOrderEndDateTime().getTime() < newMessage.getMessageTime().getTime()) {
            orderStatus.deleteNonPeriodMessages(newMessage, period);
            orderStatus.getPeriodMessages().put(queueMessage.getMessageID(), newMessage);
            orderStatus.setLastOrderDateTime(newMessage.getMessageTime());
            orderStatus.setLastOrderEndDateTime(queueMessage.getMessageEndTime());
        }
        orderStatus.getPeriodMessages().put(queueMessage.getMessageID(), newMessage);
        orderStatus.addOrder();
        orderStatus.setOrderQuantity(newMessage.getQuantity());
        if (newMessage.getTransactionType().equals(TransactionType.F)) {
            orderStatus.setOrderCancels(newMessage.getQuantity());
        }
        companyOrdersStatus.put(companyName, orderStatus);
        orderStatus.checkCancels(newMessage);
    }


}
