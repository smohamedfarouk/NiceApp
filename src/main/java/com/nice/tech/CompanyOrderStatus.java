package com.nice.tech;


import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by moham on 23/02/2016.
 */
/*
    This class contains the latest information on the processing of the file during one period say 1 second for a
    Company
        1. Company Name
        2. No of Order Messages
        3. Last order data time, Last order End date time
        4. Order cancel and quantity Cumulatives
        5. Orders in the said period
        6. Company has been already reported for excessive cancelling
 */

public class CompanyOrderStatus {
    private String companyName;
    private Date lastOrderDateTime;
    private Date lastOrderEndDateTime;
    private int noOfOrders;
    private int cancelCumulatives;
    private int orderCumulatives;
    private boolean cancelReported;
    private String cancelReportedMessage;
    private final Map<UUID, Message> periodMmessages = new ConcurrentHashMap<>();

    public CompanyOrderStatus(String companyName) {
        this.companyName = companyName;
    }

    public Map<UUID, Message> getPeriodMessages() {
        return periodMmessages;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Date getLastOrderEndDateTime() {
        return lastOrderEndDateTime;
    }

    public int getNoOfOrders() {
        return noOfOrders;
    }

    public void addOrder() {
        this.noOfOrders++;
    }

    public void setOrderCancels(int orderCancels) {
        this.cancelCumulatives = this.cancelCumulatives + orderCancels;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderCumulatives = this.orderCumulatives + orderQuantity;
    }

    public Date getLastOrderDateTime() {
        return this.lastOrderDateTime;
    }

    public void setLastOrderDateTime(Date lastOrder) {
        this.lastOrderDateTime = lastOrder;
    }

    public void setLastOrderEndDateTime(Date lastOrderEndDateTime) {
        this.lastOrderEndDateTime = lastOrderEndDateTime;
    }

    public int getCancelCumulatives() {
        return cancelCumulatives;
    }

    public int getOrderCumulatives() {
        return orderCumulatives;
    }


    /*
        This checks if the ration of cancelling is more than one third of orders and if so displays the message and
        updates the flag that the company is already reported for excessive cancelling
     */
    public boolean checkCancels(Message message) {
        if (getNoOfOrders() > 1) {
            double ratio = (double) cancelCumulatives / orderCumulatives;
            if (ratio >= 0.33333 && !cancelReported) {
                cancelReported = true;
                cancelReportedMessage =
                        "During the period " + getLastOrderDateTime() + " to " + message.getMessageTime() + " the Company \"" + getCompanyName() + "\"" + " engaged in excessive cancelling." +
                                " In this period " + ratio * 100 + "% of trades \"" + getCompanyName() + "\"" + " submitted, by quantity, were cancels.\n";

                System.out.print(cancelReportedMessage);
                return true;
            }
        }
        return false;
    }


    /*
        Gets the New Message time and goes back 60 seconds and then iterates through the messages in the
        allowed window to stay and remove from the period messages hash map the messages which are no more
        relavent and then update the stats accordingly(cancel cumulatives, order cumulatives and no of orders)
    */
    public void deleteNonPeriodMessages(Message newMessage, long lifeTime) {
        long newPeriodStartTime = newMessage.getMessageTime().getTime() - lifeTime;
        for (UUID id : periodMmessages.keySet()) {
            Message message = periodMmessages.get(id);
            long existingMessageTime = message.getMessageTime().getTime();
            if (existingMessageTime < newPeriodStartTime) {
                if (message.getTransactionType().equals(TransactionType.F)) {
                    this.cancelCumulatives = cancelCumulatives - message.getQuantity();
                }
                this.orderCumulatives = this.orderCumulatives - message.getQuantity();
                this.noOfOrders = noOfOrders - 1;
                periodMmessages.remove(id);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyOrderStatus that = (CompanyOrderStatus) o;
        return companyName.equals(that.companyName);
    }

    @Override
    public int hashCode() {
        return companyName.hashCode();
    }

    public boolean isCancelReported() {
        return cancelReported;
    }

    public String getCancelReportedMessage() {
        return cancelReportedMessage;
    }
}
