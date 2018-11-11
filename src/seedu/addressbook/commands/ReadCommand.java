//@@author ongweekeong
package seedu.addressbook.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.password.Password;

/**
 * Updates read status of message identified using it's last displayed index from the user's inbox.
 * Can only be executed once 'showunread' or 'inbox' command has been used.
 */
public class ReadCommand extends Command {
    public static final String COMMAND_WORD = "read";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Marks message as read by index displayed after \"showunread\" or \"inbox\" command is used. \n\t"
            + "Parameters: Index\n\t"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UPDATE_SUCCESS = "Message mark as read!";
    public static final String MESSAGE_INPUT_INDEX_TOO_LARGE = "Index entered too large!";

    private static final Logger logger = Logger.getLogger(InboxCommand.class.getName());

    private int index;

    private Inbox myInbox = new Inbox(Password.getId());

    public ReadCommand(int targetVisibleIndex) {
        index = targetVisibleIndex;
    }

    /**
     * TODO: Add Javadoc comment
     * @return
     */
    public CommandResult execute() {
        if (index == Integer.MAX_VALUE) {
            logger.log(Level.INFO, "Index entered too large.");

            return new CommandResult(MESSAGE_INPUT_INDEX_TOO_LARGE);
        }
        String result = myInbox.markMsgAsRead(index);
        switch (result) {
        case Inbox.MESSAGE_READ_STATUS_UPDATED:
            logger.log(Level.INFO, MESSAGE_UPDATE_SUCCESS);
            return new CommandResult(MESSAGE_UPDATE_SUCCESS);

        case Inbox.INDEX_OUT_OF_BOUNDS:
            logger.log(Level.INFO, "Index entered out of bounds.");
            return new CommandResult(String.format(Inbox.INDEX_OUT_OF_BOUNDS, myInbox.checkNumUnreadMessages()));

        case Inbox.INBOX_NOT_READ_YET:
            logger.log(Level.INFO, "Inbox not opened yet.");
            return new CommandResult(Inbox.INBOX_NOT_READ_YET);

        case Inbox.INBOX_NO_UNREAD_MESSAGES:
            logger.log(Level.INFO, "Inbox has no unread messages.");
            return new CommandResult(Inbox.INBOX_NO_UNREAD_MESSAGES);

        default:
            logger.log(Level.WARNING, "Unknown error.");
            return new CommandResult("Unknown error in updating messages.");
        }
    }

}
