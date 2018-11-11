//@@author ongweekeong
package seedu.addressbook.inbox;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.addressbook.timeanddate.TimeAndDate;

/**
 * Writes notifications sent from user to the specified recipient.
 */
public class NotificationWriter {
    private static final Logger logger = Logger.getLogger(NotificationWriter.class.getName());

    private String path;
    private boolean isAppend;

    public NotificationWriter(String userId) {
        path = MessageFilePaths.getFilePathFromUserId(userId);
        isAppend = true;
    }

    public NotificationWriter(String filePath, boolean appendValue) {
        path = filePath;
        isAppend = appendValue;
    }

    /**
     * Writes message sent by user in a specific format in the inbox storage text file of the recipient.
     */
    public void writeToFile(Msg message) throws IOException {
        logger.log(Level.INFO, String.format("Writing to \"%s\"", path));
        TimeAndDate dateFormatter = new TimeAndDate();
        FileWriter write = new FileWriter (path, isAppend);
        PrintWriter myPrinter = new PrintWriter(write);
        myPrinter.println("> START OF MESSAGE <");
        myPrinter.println("Sender ID:" + message.getSenderId());
        myPrinter.println("Read status:" + message.hasBeenRead());
        myPrinter.println("Priority:" + message.getPriority());
        myPrinter.println("Timestamp:" + dateFormatter.outputDATHrs());
        myPrinter.println("Message:" + message.getMsg());

        if (message.hasEta()) {
            myPrinter.println("ETA:" + message.getEta());
        } else {
            myPrinter.println('-');
        }

        if (message.isLocationAvailable()) {
            myPrinter.println("Location:" + message.getLatitude() + "," + message.getLongitude());
        } else {
            myPrinter.println('-');
        }

        myPrinter.println("> END OF MESSAGE <"); // Notate the end of 1 message entry with "---"

        myPrinter.close();
    }

    // Create overload function for write to file to write a set of notifications.

    /**
     * TODO: Add Javadoc comment
     * @param msgSet
     * @throws IOException
     */
    void writeToFile(TreeSet<Msg> msgSet) throws IOException {
        logger.log(Level.INFO, String.format("Updating \"%s\"", path));
        FileWriter write = new FileWriter (path, isAppend);
        PrintWriter myPrinter = new PrintWriter(write);
        int numMsg = msgSet.size();
        Msg msg;
        for (int i = 0; i < numMsg; i++) {
            msg = msgSet.pollFirst();
            myPrinter.println("> START OF MESSAGE <");
            myPrinter.println("Sender ID:" + msg.getSenderId());
            myPrinter.println("Read status:" + msg.hasBeenRead());
            myPrinter.println("Priority:" + msg.getPriority());
            myPrinter.println("Timestamp:" + TimeAndDate.outputDATHrs(msg.getTime()));
            myPrinter.println("Message:" + msg.getMsg());
            if (msg.hasEta()) {
                myPrinter.println("ETA:" + msg.getEta());
            } else {
                myPrinter.println('-');
            }
            if (msg.isLocationAvailable()) {
                myPrinter.println("Location:" + msg.getLatitude() + "," + msg.getLongitude());
            } else {
                myPrinter.println('-');
            }

            myPrinter.println("> END OF MESSAGE <");
        }

        myPrinter.close();
    }

    public static void clearInbox(String path) throws IOException {
        clearInboxFromPath(path);
    }

    /**
     * TODO: Add Javadoc comment
     * @throws IOException
     */
    public static void clearAllInbox() throws IOException {
        String[] paths = {
            MessageFilePaths.FILEPATH_HQP_INBOX,
            MessageFilePaths.FILEPATH_PO1_INBOX,
            MessageFilePaths.FILEPATH_PO2_INBOX,
            MessageFilePaths.FILEPATH_PO3_INBOX,
            MessageFilePaths.FILEPATH_PO4_INBOX,
            MessageFilePaths.FILEPATH_PO5_INBOX,
            MessageFilePaths.FILEPATH_DEFAULT
        };
        for (String myPath : paths) {
            clearInboxFromPath(myPath);
        }
    }

    private static void clearInboxFromPath(String myPath) throws IOException {
        FileWriter write = new FileWriter(myPath, false);
        PrintWriter myPrinter = new PrintWriter(write);
        myPrinter.close();
    }
}
