package com.nice.tech;

import java.util.Date;
import java.util.UUID;

/**
 * Created by moham on 22/02/2016.
 */
public class QueueMessage {
    private final UUID messageId;
    private final Message message;
    private Date messageEndTime;

    public QueueMessage(Message message, long delay) {
        this.messageId = UUID.randomUUID();
        this.message = message;
        this.messageEndTime = new Date(message.getMessageTime().getTime() + delay);
    }

    public UUID getMessageID() {
        return messageId;
    }

    public Date getMessageEndTime() {
        return messageEndTime;
    }


    public Message getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return "{" +
                "message='" + message + '\'' +
                ", messageEndTime=" + messageEndTime +
                '}';
    }

}
