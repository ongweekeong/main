//@@author ongweekeong
package seedu.addressbook.inbox;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Stores all messages read from user's inbox message storage file.
 * Also keeps track of the total number of messages and unread messages.
 */
public class Inbox {
    public static final String INBOX_NOT_READ_YET = "You have not read your inbox! \n\t"
            + "Type \"showunread\" to view your unread messages.";
    public static final String INBOX_NO_UNREAD_MESSAGES = "You have no unread messages in your inbox.";
    public static final String INDEX_OUT_OF_BOUNDS = "Index entered is out of bounds. "
            + "                                           Enter message number from 1 to %1$d.";
    public static final String MESSAGE_STORAGE_PATH_NOT_FOUND = "Cannot find file to write to.";
    public static final String MESSAGE_READ_STATUS_UPDATED = "Successful update";

    private static int numUnreadMsgs = -1;

    private static HashMap<Integer, Msg> recordNotifications = new HashMap<>();
    private static NotificationReader notificationReader;
    private static NotificationWriter allMessages;
    // all messages will be stored here, notifications will appear based on severity and timestamp.
    private static String messageStorageFilepath;
    private TreeSet<Msg> notificationsToPrint = new TreeSet<>();

    public Inbox(String policeOfficerId) {
        messageStorageFilepath = MessageFilePaths.getFilePathFromUserId(policeOfficerId);
        notificationReader = new NotificationReader(messageStorageFilepath);
        allMessages = new NotificationWriter(messageStorageFilepath, false);
    }

    public static void setNumUnreadMsgs(int numUnreadMsgs) {
        Inbox.numUnreadMsgs = numUnreadMsgs;
    }

    /**
     * Loads and stores the user's Msgs in sorted order.
     * @return
     * @throws IOException
     */
    public TreeSet<Msg> loadMsgs() throws IOException {
        notificationsToPrint = notificationReader.readFromFile();
        int messageIndex = 1;
        for (Msg message : notificationsToPrint) {
            recordNotifications.put(messageIndex++, message);
        }
        numUnreadMsgs = notificationReader.getNumUnreadMsgs();
        return notificationsToPrint;
    }

    /**
     * Messages marked as read by the user will be updated in recordNotifications.
     * Message storage file will be updated with the respective changes.
     *
     * @param index
     * @return feedback to user indicating if execution was successful or not.
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public String markMsgAsRead(int index) throws NullPointerException, IndexOutOfBoundsException {
        try {
            if ((index < 1) || (index > numUnreadMsgs)) {
                throw new IndexOutOfBoundsException();
            }
            recordNotifications.get(index).setMsgAsRead();
            for (int i = 1; i <= recordNotifications.size(); i++) {
                notificationsToPrint.add(recordNotifications.get(i));
            }
            allMessages.writeToFile(notificationsToPrint);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            if (numUnreadMsgs > 0) {
                return INDEX_OUT_OF_BOUNDS;
            } else if (numUnreadMsgs == -1) {
                return INBOX_NOT_READ_YET;
            } else if (numUnreadMsgs == 0) {
                return INBOX_NO_UNREAD_MESSAGES;
            }
        } catch (IOException e) {
            return MESSAGE_STORAGE_PATH_NOT_FOUND;
        }
        return MESSAGE_READ_STATUS_UPDATED;
    }

    public int checkNumUnreadMessages() {
        return numUnreadMsgs;
    }

    /**
     * Clears record of messages displayed to user when inbox is opened.
     * Also sets inbox into an unread state (numUnreadMsgs = -1).
     */
    public static void resetInboxWhenLogout() {
        numUnreadMsgs = -1;
        recordNotifications.clear();
    }

    /**
     * Clears the record of messages that are displayed to user when inbox is opened
     */
    public static void clearInboxRecords() {
        numUnreadMsgs = 0;
        recordNotifications.clear();
    }

    public static boolean isRecordMsgsEmpty() {
        return recordNotifications.isEmpty();
    }

}
