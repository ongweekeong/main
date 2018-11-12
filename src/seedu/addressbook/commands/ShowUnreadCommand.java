//@@author ongweekeong
package seedu.addressbook.commands;

import java.io.IOException;
import java.util.TreeSet;
import java.util.logging.*;

import seedu.addressbook.common.Messages;
import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.password.Password;

import static seedu.addressbook.parser.Parser.setupLoggerForAll;

/** Prints out all unread notifications ordered by read status, priority, then timestamp
 * (earlier message has higher priority).
 *
 * @return messages to be printed out on the main window.
 */

public class ShowUnreadCommand extends Command {

    public static final String COMMAND_WORD = "showunread";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all unread messages in the application starting from the most urgent.\n\t"
            + "Example: " + COMMAND_WORD;

    private static final Logger logger = Logger.getLogger(ShowUnreadCommand.class.getName());

    private static void setupLogger() {
        setupLoggerForAll(logger);
    }


    @Override
    public CommandResult execute() {
        setupLogger();
        Inbox myInbox = new Inbox(Password.getId());
        TreeSet<Msg> allMsgs;
        int myUnreadMsgs;
        int messageNum = 1;
        Msg msgToPrint;
        try {
            logger.log(Level.INFO, String.format("Reading messages from \"%s\"", myInbox));

            allMsgs = myInbox.loadMsgs();
            myUnreadMsgs = myInbox.checkNumUnreadMessages();
            if (myUnreadMsgs != 0) {
                StringBuilder fullPrintedMessage = new StringBuilder(Messages.MESSAGE_UNREAD_MSG_NOTIFICATION + '\n');
                for (int i = 0; i < myUnreadMsgs; i++) {
                    msgToPrint = allMsgs.pollFirst();
                    fullPrintedMessage.append(InboxCommand.concatenateMsg(messageNum, msgToPrint));
                    messageNum++;
                }
                allMsgs.clear();
                return new CommandResult(String.format(fullPrintedMessage.toString(), myUnreadMsgs));
            } else {
                allMsgs.clear();
                return new CommandResult(Messages.MESSAGE_NO_UNREAD_MSGS);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("\"%s\" not found", myInbox));

            e.printStackTrace();
            return new CommandResult("Error loading messages.");
        }
    }

}
