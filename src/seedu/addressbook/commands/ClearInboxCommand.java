//@@author ongweekeong
package seedu.addressbook.commands;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.NotificationReader;
import seedu.addressbook.inbox.NotificationWriter;
import seedu.addressbook.password.Password;

import static seedu.addressbook.parser.Parser.SetupLogger;

/**
 * Clears the text file storing the messages sent to user.
 */
public class ClearInboxCommand extends Command {

    public static final String COMMAND_WORD = "clearinbox";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Clears all existing messages in user's inbox. \n\t"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_CLEARINBOX_SUCCESSFUL = "Inbox cleared!";
    public static final String MESSAGE_CLEARINBOX_UNSUCCESSFUL = "Unable to clear inbox. Missing inbox storage file.";
    private static final Logger logger = Logger.getLogger(InboxCommand.class.getName());

    private static void setupLogger() {
        SetupLogger(logger);
    }

    private String myInbox;

    public ClearInboxCommand(){

    }
    public ClearInboxCommand(String filepath) {
        myInbox = filepath;
    }

    @Override
    public CommandResult execute() {
        setupLogger();
        try {
            if (myInbox == null) {
                myInbox = MessageFilePaths.getFilePathFromUserId(Password.getId());
            }
            logger.log(Level.INFO, String.format("Clearing messages in \"%s\"", myInbox));

            NotificationReader dummyReader = new NotificationReader(myInbox);
            dummyReader.readFromFile(); // Exception thrown if file does not exist
            NotificationWriter.clearInbox(myInbox);
            Inbox.clearInboxRecords();
            return new CommandResult(MESSAGE_CLEARINBOX_SUCCESSFUL);
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("\"%s\" not found", myInbox));

            return new CommandResult(MESSAGE_CLEARINBOX_UNSUCCESSFUL);
        }

    }
}
