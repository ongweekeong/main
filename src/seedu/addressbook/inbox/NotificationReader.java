//@@author ongweekeong
package seedu.addressbook.inbox;

import seedu.addressbook.common.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

/**
 * Reads the respective text files containing the messages sent to the user.
 */
public class NotificationReader {
    private static String path;
    private Msg returnMsg;
    private int unreadMsgs = 0;
    private TreeSet<Msg> sortedMsgs = new TreeSet<>();

    public NotificationReader(String filePath) {
        path = filePath;
    }

    /**
     * Method parses the messages stored and converts them into Msg objects containing the relevant information.
     *
     * @return a sorted set of Msgs that were found in the user's inbox storage file.
     * @throws IOException
     */
    public TreeSet<Msg>
    ReadFromFile() throws IOException {  // If no new notifications and 'inbox' command invoked, show past 10 notifications
        String line;
        BufferedReader br = new BufferedReader(new FileReader(path));
        unreadMsgs = 0;
        while ((line = br.readLine()) != null) {
            switch (line) {
                case "> START OF MESSAGE <":
                    returnMsg = new Msg();
                    break;
                case "> END OF MESSAGE <":   // End of message entry, store into TreeSet
                    sortedMsgs.add(returnMsg);
                    break;
                default:
                    String[] parts = line.split(":", 2);
                    String msgType = parts[0];
                    if (parts.length == 2) {
                        switch (msgType) {
                            case "Sender ID":
                                readMsgSenderId(parts[1]);
                                break;
                            case "Read status":
                                readMsgReadStatus(parts[1]);
                                if (!returnMsg.hasBeenRead())
                                    unreadMsgs += 1;
                                break;
                            case "Priority":
                                readMsgPriority(parts[1]);
                                break;
                            case "Timestamp":
                                readMsgTimestamp(parts[1]);
                                break;
                            case "Message":
                                readMsgMessage(parts[1]);
                                break;
                            case "Location":
                                readMsgLocation(parts[1]);
                                break;
                        }
                    }

                    break;
            }
        }
        return sortedMsgs;
    }

    int getNumUnreadMsgs(){
        return unreadMsgs;
    }

    private void readMsgSenderId(String userId){
        returnMsg.setSenderId(userId);
    }

    private void readMsgReadStatus(String readStatus) {
        returnMsg.setReadStatus(Boolean.parseBoolean(readStatus));
    }

    private void readMsgPriority(String priority){
        Msg.Priority msgPriority;
        switch (priority) {
            case "HIGH":
                msgPriority = Msg.Priority.HIGH;
                break;
            case "MED":
                msgPriority = Msg.Priority.MED;
                break;
            default:
                msgPriority = Msg.Priority.LOW;
                break;
        }

        returnMsg.setPriority(msgPriority);
    }


    private void readMsgTimestamp(String timestamp){
        SimpleDateFormat timeFormatted = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss:SSS");
        Date parsedTimeStamp;
        try {
            parsedTimeStamp = timeFormatted.parse(timestamp);
        } catch (ParseException e) {
            parsedTimeStamp = new Date(0,1,1,0,0,0);
        }
        Timestamp msgTime = new Timestamp(parsedTimeStamp.getTime());
        returnMsg.setTime(msgTime);
    }

    private void readMsgMessage(String message){
        returnMsg.setMsg(message);
    }

    private void readMsgLocation(String xyValue){
        String[] coordinates = xyValue.split(",", 2);
        Location myLocation = new Location(Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]));
        returnMsg.setLocation(myLocation);
    }

}