package com.nice.tech;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by moham on 22/02/2016.
 */
public class Message {
    private final Date messageTime;
    private final String companyName;
    private final TransactionType transactionType;
    private final int quantity;
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public Message(Date messageTime, String companyName, TransactionType type, int quantity) {
        this.messageTime = messageTime;
        this.companyName = companyName;
        this.transactionType = type;
        this.quantity = quantity;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        if (quantity != message.quantity) return false;
        if (messageTime != null ? !messageTime.equals(message.messageTime) : message.messageTime != null) return false;
        if (companyName != null ? !companyName.equals(message.companyName) : message.companyName != null) return false;
        return transactionType == message.transactionType;
    }

    @Override
    public int hashCode() {
        int result = messageTime != null ? messageTime.hashCode() : 0;
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (transactionType != null ? transactionType.hashCode() : 0);
        result = 31 * result + quantity;
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageTime=" + messageTime +
                ", companyName='" + companyName + '\'' +
                ", transactionIndicator=" + transactionType +
                ", quantity=" + quantity +
                '}';
    }
}
