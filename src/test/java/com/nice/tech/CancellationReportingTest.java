package com.nice.tech;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

/**
 * Write a program to process the data file and print a line to the console when it detects that a company is engaged in excessive cancelling.
 * <p/>
 * 2015-05-21 16:23:00,AGreedy bankers ltd.,D,10    // Test Case 1 - To be reported Cancelling within the same second
 * 2015-05-21 16:23:00,AGreedy bankers ltd.,F,10    // Test Case 1 - To be reported Cancelling within the same second
 * <p/>
 * 2015-05-21 16:23:01,BGreedy bankers ltd.,D,10    // Test Case 2 - Not to be reported no cancellation within the same second
 * 2015-05-21 16:23:01,BGreedy bankers ltd.,D,10    // Test Case 2 - Not to be reported no cancellation within the same second
 * <p/>
 * 2015-05-21 16:23:00,CGreedy bankers ltd.,D,10    // Test Case 3 - To be reported Cancelling within the window of 1 second
 * 2015-05-21 16:23:01,CGreedy bankers ltd.,F,10    // Test Case 3 - To be reported Cancelling within the window of 1 second
 * 2015-05-21 16:23:02,CCGreedy bankers ltd.,D,10   // Test Case 3 - To be reported Cancelling within the window of 1 second
 * 2015-05-21 16:23:03,CGreedy bankers ltd.,D,10    // Test Case 3  - To be reported Cancelling within the window of 1 second
 * 2015-05-21 16:23:35,CGreedy bankers ltd.,D,15    // Test Case 3 - To be reported Cancelling within the window of 4 second
 * 2015-05-21 16:23:39,CGreedy bankers ltd.,D,10    // Test Case 3 - To be reported Cancelling within the window of 4 second
 * <p/>
 * 2015-05-21 16:23:10,DGreedy bankers ltd.,D,15    // Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
 * 2015-05-21 16:23:13,DGreedy bankers ltd.,F,20    // Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
 * <p/>
 * 2015-05-21 16:23:10,DGreedy bankers ltd.,D,15   // Test Case 5 - Not To be reported Cancelling within the window of 4 second as already reported
 * 2015-05-21 16:23:13,DGreedy bankers ltd.,F,20   // Test Case 5 - Not To be reported Cancelling within the window of 4 second as already reported
 * 2015-05-21 16:24:10,DGreedy bankers ltd.,D,15   // Test Case 5 - Not To be reported Cancelling within the window of 4 second as already reported
 * 2015-05-21 16:24:13,DGreedy bankers ltd.,F,20   // Test Case 5 - Not To be reported Cancelling within the window of 4 second as already reported
 * <p/>
 * 2015-05-21 16:23:20,EGreedy bankers ltd.,D,15   // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
 * 2015-05-21 16:23:20,EGreedy bankers ltd.,F,4    // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
 * 2015-05-21 16:23:22,EGreedy bankers ltd.,D,15   // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
 * 2015-05-21 16:23:22,EGreedy bankers ltd.,F,4    // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
 * <p/>
 * 2015-05-21 16:23:20,FGreedy bankers ltd.,D,15   // Test Case 7 - Check period message sizes after cleaning without Cancellations (with 5 second window)- Should be removed after cleaning
 * 2015-05-21 16:23:20,FGreedy bankers ltd.,D,15   // Test Case 7 - Check period message sizes after cleaning (with 5 second window)(with 5 second window) - Should be removed after cleaning
 * 2015-05-21 16:23:21,FGreedy bankers ltd.,D,2    // Test Case 7 - Check period message sizes after cleaning (with 5 second window)
 * 2015-05-21 16:23:26,FGreedy bankers ltd.,D,10   // Test Case 7 - Check period message sizes after cleaning (with 5 second window)
 * 2015-05-21 16:23:26,FGreedy bankers ltd.,F,4   // Test Case 7 - Check period message sizes after cleaning (with 5 second window)
 * <p/>
 * 2015-05-21 16:23:20,GGreedy bankers ltd.,D,15   // Test Case 8 - Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)  - Should be removed after cleaning
 * 2015-05-21 16:23:20,GGreedy bankers ltd.,D,15   // Test Case 8- Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)- Should be removed after cleaning
 * 2015-05-21 16:23:21,GGreedy bankers ltd.,D,2    // Test Case 8 - Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)
 * 2015-05-21 16:23:26,GGreedy bankers ltd.,D,10   // Test Case 8 - Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)
 * 2015-05-21 16:23:26,GGreedy bankers ltd.,F,15   // Test Case 8 - Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)
 */
public class CancellationReportingTest {
    private static final long period = 5000l;

    /*
       Test Case 1 - To be reported Cancelling within the same second
        2015-05-21 16:23:00,AGreedy bankers ltd.,D,10
        2015-05-21 16:23:00,AGreedy bankers ltd.,F,10
     */
    @Test
    public void reportCancellingWithinTheSameSecond() throws ExecutionException, InterruptedException, IOException {
        //Test Case 1 - To be reported Cancelling within the same second
        String str1 = "2015-05-21 16:23:00,AGreedy bankers ltd.,D,10";
        String str2 = "2015-05-21 16:23:00,AGreedy bankers ltd.,F,10";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        String expectedMessage = "During the period Thu May 21 16:23:00 BST 2015 to Thu May 21 16:23:00 BST 2015 the Company \"AGreedy bankers ltd.\" engaged in excessive cancelling. In this period 50.0% of trades \"AGreedy bankers ltd.\" submitted, by quantity, were cancels.\n";
        assertTrue(main.getCompanyOrdersStatus().get("AGreedy bankers ltd.").isCancelReported() == true);
        assertTrue(main.getCompanyOrdersStatus().get("AGreedy bankers ltd.").getCancelReportedMessage().equalsIgnoreCase(expectedMessage));
    }


    /*
      Test Case 2 - Not to be reported no cancellation within the same second
      2015-05-21 16:23:01,BGreedy bankers ltd.,D,10
      2015-05-21 16:23:01,BGreedy bankers ltd.,D,10
    */
    @Test
    public void reportNoCancellingWithinTheSameSecond() throws ExecutionException, InterruptedException, IOException {
        //Test Case 1 - To be reported Cancelling within the same second
        String str1 = "2015-05-21 16:23:01,BGreedy bankers ltd.,D,10";
        String str2 = "2015-05-21 16:23:01,BGreedy bankers ltd.,D,10";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        //String expectedMessage = "During the period Thu May 21 16:23:00 BST 2015 to Thu May 21 16:23:00 BST 2015 the Company \"AGreedy bankers ltd.\" engaged in excessive cancelling. In this period 50.0% of trades \"AGreedy bankers ltd.\" submitted, by quantity, were cancels.\n";
        assertTrue(main.getCompanyOrdersStatus().get("BGreedy bankers ltd.").isCancelReported() == false);
        assertTrue(main.getCompanyOrdersStatus().get("BGreedy bankers ltd.").getCancelReportedMessage() == null);
    }

    /*
   Test Case 4 - To be reported Cancelling within the window of 1 second
     2015-05-21 16:23:00,CGreedy bankers ltd.,D,10
     2015-05-21 16:23:01,CGreedy bankers ltd.,F,10
     2015-05-21 16:23:02,CCGreedy bankers ltd.,D,10
     2015-05-21 16:23:03,CGreedy bankers ltd.,D,10
     2015-05-21 16:23:35,CGreedy bankers ltd.,D,15
     2015-05-21 16:23:39,CGreedy bankers ltd.,D,10
   */
    @Test
    public void reportCancellationWithinOneSecondWindow() throws ExecutionException, InterruptedException, IOException {
        //Test Case 1 - To be reported Cancelling within the same second
        String str1 = "2015-05-21 16:23:00,CGreedy bankers ltd.,D,10";
        String str2 = "2015-05-21 16:23:01,CGreedy bankers ltd.,F,10";
        String str3 = "2015-05-21 16:23:02,CGreedy bankers ltd.,D,10";
        String str4 = "2015-05-21 16:23:03,CGreedy bankers ltd.,D,10";
        String str5 = "2015-05-21 16:23:35,CGreedy bankers ltd.,D,15";
        String str6 = "2015-05-21 16:23:39,CGreedy bankers ltd.,D,10";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.write(str3);
        bw.newLine();
        bw.write(str4);
        bw.newLine();
        bw.write(str5);
        bw.newLine();
        bw.write(str6);
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        //String expectedMessage = "During the period Thu May 21 16:23:00 BST 2015 to Thu May 21 16:23:00 BST 2015 the Company \"AGreedy bankers ltd.\" engaged in excessive cancelling. In this period 50.0% of trades \"AGreedy bankers ltd.\" submitted, by quantity, were cancels.\n";
        assertTrue(main.getCompanyOrdersStatus().get("CGreedy bankers ltd.").isCancelReported() == true);
        assertTrue(main.getCompanyOrdersStatus().get("CGreedy bankers ltd.").getCancelReportedMessage() != null);
    }

    /*
    Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
    2015-05-21 16:23:10,DGreedy bankers ltd.,D,15
    2015-05-21 16:23:13,DGreedy bankers ltd.,F,20
   */
    @Test
    public void reportCancellationWithinTheWindowOf4Seconds() throws ExecutionException, InterruptedException, IOException {
        //Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
        String str1 = "2015-05-21 16:23:10,DGreedy bankers ltd.,D,15";
        String str2 = "2015-05-21 16:23:13,DGreedy bankers ltd.,F,20";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        //String expectedMessage = "During the period Thu May 21 16:23:00 BST 2015 to Thu May 21 16:23:00 BST 2015 the Company \"AGreedy bankers ltd.\" engaged in excessive cancelling. In this period 50.0% of trades \"AGreedy bankers ltd.\" submitted, by quantity, were cancels.\n";
        assertTrue(main.getCompanyOrdersStatus().get("DGreedy bankers ltd.").isCancelReported() == true);
        assertTrue(main.getCompanyOrdersStatus().get("DGreedy bankers ltd.").getCancelReportedMessage() != null);
    }

    /*
   Test Case 5  - Not To be reported Cancelling within the window of 4 second as already reported for the same company
        2015-05-21 16:23:10,DGreedy bankers ltd.,D,15
        2015-05-21 16:23:13,DGreedy bankers ltd.,F,20
        2015-05-21 16:24:10,DGreedy bankers ltd.,D,15
        2015-05-21 16:24:13,DGreedy bankers ltd.,F,20
  */
    @Test
    public void notToReportCancellingIfAlreadyReportedForTheSameCompany() throws ExecutionException, InterruptedException, IOException {
        //Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
        String str1 = "2015-05-21 16:23:10,DGreedy bankers ltd.,D,15";
        String str2 = "2015-05-21 16:23:13,DGreedy bankers ltd.,F,20";
        String str3 = "2015-05-21 16:24:10,DGreedy bankers ltd.,D,15";
        String str4 = "2015-05-21 16:24:13,DGreedy bankers ltd.,F,20";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.write(str3);
        bw.newLine();
        bw.write(str4);
        bw.newLine();
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        String expectedMessage = "During the period Thu May 21 16:23:10 BST 2015 to Thu May 21 16:23:13 BST 2015 the Company \"DGreedy bankers ltd.\" engaged in excessive cancelling. In this period 57.14285714285714% of trades \"DGreedy bankers ltd.\" submitted, by quantity, were cancels.\n";
        assertTrue(main.getCompanyOrdersStatus().get("DGreedy bankers ltd.").isCancelReported() == true);
        assertTrue(main.getCompanyOrdersStatus().get("DGreedy bankers ltd.").getCancelReportedMessage().equalsIgnoreCase(expectedMessage));
    }
    /*
    Test Case 6  -  Check Cumulatives (Cancel and Order totals) (with 5 second window)
        2015-05-21 16:23:20,EGreedy bankers ltd.,D,15   // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
        2015-05-21 16:23:20,EGreedy bankers ltd.,F,4    // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
        2015-05-21 16:23:22,EGreedy bankers ltd.,D,15   // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
        2015-05-21 16:23:22,EGreedy bankers ltd.,F,4    // Test Case 6 - Check Cumulatives (Cancel and Order totals) (with 5 second window)
      */

    @Test
    public void checkCumulatives() throws ExecutionException, InterruptedException, IOException {
        //Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
        String str1 = "2015-05-21 16:23:20,EGreedy bankers ltd.,D,15";
        String str2 = "2015-05-21 16:23:20,EGreedy bankers ltd.,F,4";
        String str3 = "2015-05-21 16:23:22,EGreedy bankers ltd.,D,15";
        String str4 = "2015-05-21 16:23:22,EGreedy bankers ltd.,F,4";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.write(str3);
        bw.newLine();
        bw.write(str4);
        bw.newLine();
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        CompanyOrderStatus orderStatus = main.getCompanyOrdersStatus().get("EGreedy bankers ltd.");
        assertTrue(orderStatus.getNoOfOrders() == 4);
        assertTrue(orderStatus.getOrderCumulatives() == 38);
        assertTrue(orderStatus.getCancelCumulatives() == 8);
        assertTrue(orderStatus.isCancelReported() == false);
        assertTrue(orderStatus.getCancelReportedMessage() == null);
    }

    /*
    // Test Case 7 - Check period message sizes after cleaning (with 5 second window) - Should be removed after cleaning
          2015-05-21 16:23:20,FGreedy bankers ltd.,D,15
         2015-05-21 16:23:20,FGreedy bankers ltd.,D,15
         2015-05-21 16:23:21,FGreedy bankers ltd.,D,2
         2015-05-21 16:23:26,FGreedy bankers ltd.,D,10
         2015-05-21 16:23:26,FGreedy bankers ltd.,F,4
*/
    @Test
    public void checkPeriodMessageSizesCleaningWithoutCancellations() throws ExecutionException, InterruptedException, IOException {
        //Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
        String str1 = "2015-05-21 16:23:20,FGreedy bankers ltd.,D,15";
        String str2 = "2015-05-21 16:23:20,FGreedy bankers ltd.,D,15";
        String str3 = "2015-05-21 16:23:21,FGreedy bankers ltd.,D,2";
        String str4 = "2015-05-21 16:23:26,FGreedy bankers ltd.,D,10";
        String str5 = "2015-05-21 16:23:26,FGreedy bankers ltd.,F,4";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.write(str3);
        bw.newLine();
        bw.write(str4);
        bw.newLine();
        bw.write(str5);
        bw.newLine();
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        CompanyOrderStatus orderStatus = main.getCompanyOrdersStatus().get("FGreedy bankers ltd.");
        assertTrue(orderStatus.getNoOfOrders() == 3);
        assertTrue(orderStatus.getPeriodMessages().size() == 3);
        assertTrue(orderStatus.getOrderCumulatives() == 16);
        assertTrue(orderStatus.getCancelCumulatives() == 4);
        assertTrue(orderStatus.isCancelReported() == false);
        assertTrue(orderStatus.getCancelReportedMessage() == null);
    }

    /*
  // Test Case 8 - // Test Case 8 - Check Cumulatives (Cancel and order totals)  sizes after cleaning (with 5 second window)  - Should be removed after cleaning
       2015-05-21 16:23:20,GGreedy bankers ltd.,D,15
       2015-05-21 16:23:20,GGreedy bankers ltd.,D,15
       2015-05-21 16:23:21,GGreedy bankers ltd.,D,2
       2015-05-21 16:23:26,GGreedy bankers ltd.,D,10
       2015-05-21 16:23:26,GGreedy bankers ltd.,F,15
*/
    @Test
    public void checkPeriodMessageSizesWithCleaningAndCancellations() throws ExecutionException, InterruptedException, IOException {
        //Test Case 4  - To be reported Cancelling within the window of 4 second (time less than actual window seconds)
        String str1 = "2015-05-21 16:23:20,GGreedy bankers ltd.,D,15";
        String str2 = "2015-05-21 16:23:20,GGreedy bankers ltd.,D,15";
        String str3 = "2015-05-21 16:23:21,GGreedy bankers ltd.,D,2";
        String str4 = "2015-05-21 16:23:26,GGreedy bankers ltd.,D,10";
        String str5 = "2015-05-21 16:23:26,GGreedy bankers ltd.,F,15";
        File temp = File.createTempFile("tempfile", ".tmp");
        Map<String, Throwable> fileExceptions = new HashMap();
        List<String> excessiveCancellationsReport = new LinkedList<String>();
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(str1);
        bw.newLine();
        bw.write(str2);
        bw.newLine();
        bw.write(str3);
        bw.newLine();
        bw.write(str4);
        bw.newLine();
        bw.write(str5);
        bw.newLine();
        bw.close();
        EventNotifyingDeque queue = new EventNotifyingDeque();
        Map<String, CompanyOrderStatus> companyOrdersStatus = new ConcurrentHashMap<>();
        App main = new App(queue, companyOrdersStatus, period);
        main.start(temp, fileExceptions, excessiveCancellationsReport);
        assertTrue(fileExceptions.size() == 0);
        assertTrue(main.getCompanyOrdersStatus().size() == 1);
        CompanyOrderStatus orderStatus = main.getCompanyOrdersStatus().get("GGreedy bankers ltd.");
        assertTrue(orderStatus.getNoOfOrders() == 3);
        assertTrue(orderStatus.getPeriodMessages().size() == 3);
        assertTrue(orderStatus.getOrderCumulatives() == 27);
        assertTrue(orderStatus.getCancelCumulatives() == 15);
        assertTrue(orderStatus.isCancelReported() == true);
        assertTrue(orderStatus.getCancelReportedMessage() != null);
    }
}
