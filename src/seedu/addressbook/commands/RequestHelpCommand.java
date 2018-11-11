//@@author andyrobert3
package seedu.addressbook.commands;

import java.io.IOException;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.inbox.NotificationWriter;
import seedu.addressbook.password.Password;

/**
 * TODO: Add Javadoc comment
 */
public class RequestHelpCommand extends Command {
    public static final String COMMAND_WORD = "rb";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Requests help from headquarters.\n\t"
            + "Message from police officer can be appended in text.\n\t"
            + "Example: " + COMMAND_WORD
            + " gun";


    private static String messageRequestSuccess = "Request for backup case from %s has been sent to HQP.";

    private static Msg requestHelpMessage;
    private NotificationWriter notificationWriter;

    /**
     * Constructor for the Writers to write to headquarters personnel file.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public RequestHelpCommand(String caseName, String messageString) throws IllegalValueException {
        notificationWriter = new NotificationWriter(MessageFilePaths.FILEPATH_HQP_INBOX, true);
        requestHelpMessage = new Msg(Offense.getPriority(caseName), messageString,
                PatrolResourceStatus.getLocation(Password.getId()));
    }

    public static String getMessageRequestSuccess() {
        return messageRequestSuccess;
    }

    @Override
    public CommandResult execute() {
        try {
            notificationWriter.writeToFile(requestHelpMessage);
            PatrolResourceStatus.setStatus(Password.getId(), true);
            return new CommandResult(String.format(messageRequestSuccess, Password.getId()));
        } catch (IOException ioe) {
            return new CommandResult(Messages.MESSAGE_SAVE_ERROR);
        } catch (IllegalValueException ive) {
            return new CommandResult(Messages.MESSAGE_PO_NOT_FOUND);
        }
    }

    public static void resetRecentMessage() {
        requestHelpMessage = null;
    }

    /**
     * Retrieves most recent Msg from request command
     * @return most recent Msg
     */
    public static Msg getRecentMsg() {
        if (requestHelpMessage == null) {
            String messageRecentMessageEmpty = "Request command was never called";
            throw new NullPointerException(messageRecentMessageEmpty);
        }
        return requestHelpMessage;
    }

}
