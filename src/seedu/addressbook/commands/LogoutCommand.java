package seedu.addressbook.commands;

import static seedu.addressbook.password.Password.lockDevice;

import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.timeanddate.TimeAndDate;


//@@author iamputradanish

/**
 * Logs the user out of the System immediately. Resets all boolean flags to start up state.
 */
public class LogoutCommand extends Command {
    public static final String COMMAND_WORD = "logout";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Logs current user out of the device. No COMMANDS are allowed until correct password is entered.\n\t "
            + "Example: " + COMMAND_WORD;

    private static TimeAndDate tad = new TimeAndDate();

    public static final String MESSAGE_LOCK = "You are logged out from the device."
            + "\n" + tad.outputDatMainHrs()
            + "\n" + "Please enter password:\n";

    @Override
    public CommandResult execute() {
        Inbox.resetInboxWhenLogout();
        lockDevice();
        return new CommandResult(MESSAGE_LOCK); }
}
