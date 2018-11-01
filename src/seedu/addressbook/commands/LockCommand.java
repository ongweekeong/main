package seedu.addressbook.commands;

import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;

//@@author iamputradanish
public class LockCommand extends Command {
    private static TimeAndDate tad = new TimeAndDate();

    public static final String COMMAND_WORD = "lock";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Locks the device. No commands are allowed until correct password is entered.\n\t "
            + "Example: " + COMMAND_WORD ;
    private static final String MESSAGE_LOCK = "Your device has been locked."
            + "\n" + tad.outputDatMainHrs()
            + "\n" + "Please enter password:\n";

    @Override
    public CommandResult execute() {
        Password password  = new Password();
        password.lockDevice();
        return new CommandResult(MESSAGE_LOCK); }
}
