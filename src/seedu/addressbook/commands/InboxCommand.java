//@@author ongweekeong
package seedu.addressbook.commands;

import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;

import java.io.IOException;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Prints out total number of notifications, and all notifications ordered by read status, priority, then timestamp
 * (earlier message has higher priority).
 *
 * @return messages to be displayed on the main window.
 */
public class InboxCommand extends Command {
    private static final Logger logger = Logger.getLogger(InboxCommand.class.getName());

    public static final String COMMAND_WORD = "inbox";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all messages in the application starting from the unread and most urgent.\n\t"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_TOTAL_MESSAGE_NOTIFICATION = "You have %d total messages, %d unread.\n";
    public static final String MESSAGE_UNKNOWN_ERROR = "Error loading messages. %s cannot be found.\n";

    @Override
    public CommandResult execute() {

        Inbox myInbox = new Inbox(Password.getID());
        TreeSet<Msg> allMsgs;
        int myUnreadMsgs;
        int totalMsgs;
        int messageNum = 1;
        try {
            logger.log(Level.INFO, String.format("Reading messages from \"%s\"", myInbox));

            allMsgs = myInbox.loadMsgs();
            totalMsgs = allMsgs.size();
            myUnreadMsgs = myInbox.checkNumUnreadMessages();
            String fullPrintedMessage = MESSAGE_TOTAL_MESSAGE_NOTIFICATION;
            for (Msg msgToPrint : allMsgs) {
                fullPrintedMessage += concatenateMsg(messageNum, msgToPrint);
                messageNum++;
            }
            allMsgs.clear();
            return new CommandResult(String.format(fullPrintedMessage, totalMsgs, myUnreadMsgs));

        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("\"%s\" not found", myInbox));

            e.printStackTrace();
            return new CommandResult(String.format(MESSAGE_UNKNOWN_ERROR,
                                        MessageFilePaths.getFilePathFromUserId(Password.getID())));
        }
    }

    /**
     * Returns the everything to be displayed on the main window in a single string.
     * Method adds the individual messages below the sentence informing users how
     * many messages (total and unread) they have.
     *
     * @param messageNum
     * @param message
     * @return
     * @throws NullPointerException
     */
    public static String concatenateMsg(int messageNum, Msg message) throws NullPointerException{
        String concatenatedMsg;
        if (!message.hasBeenRead()) {
            concatenatedMsg = String.valueOf(messageNum) + ". [UNREAD] Sender: " + message.getSenderId() +
                    " Priority: " + message.getPriority() + ", Sent: " +
                    TimeAndDate.outputDATHrsForMain(message.getTime()) + ",\n\tMessage: " + message.getMsg() + "\n\n";
        }
        else{
            concatenatedMsg = String.valueOf(messageNum) + ".\tSender: " + message.getSenderId() + " Priority: " +
                    message.getPriority() + ", Sent: " + TimeAndDate.outputDATHrsForMain(message.getTime()) +
                    ",\n\tMessage: " + message.getMsg() + "\n\n";
        }

        return concatenatedMsg;
    }

}

