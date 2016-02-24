package com.nice.tech;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by moham on 22/02/2016.
 */
public class MessageProducer implements Producer {

    private final EventNotifyingDeque messages;
    private final long lifeTime;
    public static final String POISON_PILL_MESSAGE = "END OF FILE";
    private final File file;
    private final Map<String, Throwable> fileExceptions;
    private final  List<String>  excessiveCancellationReport;

    public MessageProducer(EventNotifyingDeque messages, long lifetime, File file,
                           final Map<String, Throwable> fileExceptions, List<String> excessiveCancellationReport) {
        this.messages = messages;
        this.lifeTime = lifetime;
        this.file = file;
        this.fileExceptions = fileExceptions;
        this.excessiveCancellationReport =excessiveCancellationReport;
    }


    public void run() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                produce(str);
            }
            in.close();
            produce(POISON_PILL_MESSAGE);
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
    }

    @Override
    public void produce(String str) {
        // Read each line of the file, validate it and then parse and populate to create message object;
        Message message = null;
        try {
            message = new MessageFactory().validate(str);
        } catch (Throwable e) {
            fileExceptions.put(str, e);
        }
        if (message != null) {
            try {
                QueueMessage queueMessage = new QueueMessage(message, lifeTime);
                messages.put(queueMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}