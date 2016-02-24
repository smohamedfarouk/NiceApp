package com.nice.tech;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

/**
 * Write a program to process the data file and print a line to the console when it detects that a company is engaged in excessive cancelling.
 */
public class MessageValidationTest {
    private static final long period = 60000l;

    @Test
    public void validateMessageWithIncorrectNoOfStringTokens() throws ExecutionException, InterruptedException, IOException {
        //Invalid Tokens
        String str = "2015-05-21 16:35:38";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions,excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 1);
        Throwable exception = fileExceptions.get(str);
        assertTrue((exception instanceof ArrayIndexOutOfBoundsException));
    }

    @Test
    public void validateMessageWithIncorrectDate() throws ExecutionException, InterruptedException, IOException {
        String str = "2015-05-2116:35:28,Monkey traders,D,6254";
        //Invalid Date
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions,excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 1);
        Throwable exception = fileExceptions.get(str);
        assertTrue((exception instanceof ParseException));

    }

    @Test
    public void validateMessageWithIncorrectQuantity() throws ExecutionException, InterruptedException, IOException {
        String str = "2015-05-21 16:35:28,Monkey traders,D, 6254";
        //Invalid Quantity
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions,excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 1);
        Throwable exception = fileExceptions.get(str);
        assertTrue((exception instanceof NumberFormatException));
    }

    @Test
    public void validateMessageWithSpace() throws ExecutionException, InterruptedException, IOException {
        String str = "2015-05-21 16:35:28,Monkey traders, D,6254";
        //Invalid Quantity
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions,excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 1);
        Throwable exception = fileExceptions.get(str);
        assertTrue((exception instanceof IllegalArgumentException));
    }

    @Test
    public void validateMessageValid() throws ExecutionException, InterruptedException, IOException {
        String str = "2015-05-21 16:35:28,Monkey traders,D,6254";
        //Fine Record
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions,excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
    }

}
