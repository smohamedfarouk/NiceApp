Requirement:
Write a program to process the data file and print a line to the console when it detects that a company is engaged in excessive cancelling.

Java JDK:   jdk1.7.0_80
Full Source code available from github URL: https://github.com/smohamedfarouk/NiceApp/tree/master/src/main/java/com/nice/tech
Full Test   code available from github URL: https://github.com/smohamedfarouk/NiceApp/tree/master/src/test/java/com/nice/tech
Java Documentation available from github URL: https://github.com/smohamedfarouk/NiceApp/tree/master/javadoc

Design Choices
 * Used: Poison pill pattern for termination of the Queue
 * Used: Message identifier as UUID from Java
 * Used: Factory Pattern for the Message creation
 * Used: Deque as we can take both from begining of the queue or end of the queue
 * Used: Factory Pattern for the Message creation
 * Used: Extended the Deque to provide event notification to the Main application for processing Messages
 * Used: Java Executors and ThreadPool for aysnchronous execution
 * Used: Consumer and producer pattern using basic java available synchronizers
 * Used: Exception design - Currently exceptions thrown while processing file reading will be available from the main application using a
         Map<String, Exception> fileExceptions = new HashMap();
 * Used: Just one thread for producer and one for consumer to avoid more race conditions

 Assumptions under following Scenario:
  * Scenario: Cancellation only within the same period when we have a time period with only cancel orders we still
              consider them for excessive cancelling checks
  * Scenario: when within the same period we have more cancellations than order, we still consider them for excessive
              cancelling checks

Packaging:
 1. Zip contains a SRC folder with all the code (NiceAppSrc.zip)
 2. Executable java jar file
 3. Java Doc

Run Instructions (Executable/Maven):
1. Create a directory called NiceApp in c drive
2. Copy the Zip file NiceAppSrc.zip to the newly created directory
3. cd NiceAppSrc folder
4. Make sure you have java installed in the computer
5. Start the application using  java - jar nice-1.0.jar
6. mvn clean install (should show the results of run and tests)








