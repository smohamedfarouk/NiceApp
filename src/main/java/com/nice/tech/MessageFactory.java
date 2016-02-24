package com.nice.tech;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by moham on 22/02/2016.
 */
public class MessageFactory {
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String NO_CLIENT = "NO_CLIENT";

    public Message validate(String str) throws ParseException {
        Message message = null;
        if (str.equals(MessageProducer.POISON_PILL_MESSAGE)) {
            return createPoisonMessage();
        }
        String[] ar = str.split(",");
        message = new Message(formatter.parse(ar[0]), ar[1], TransactionType.valueOf(ar[2]), Integer.parseInt(ar[3]));

        return message;
    }

    private Message createPoisonMessage() {
        return new Message(new Date(), NO_CLIENT, TransactionType.D, 0);
    }

    public static boolean isMessagePoison(Message message) {
        if (message.getCompanyName().equals(NO_CLIENT)) {
            return true;
        }
        return false;
    }

}
