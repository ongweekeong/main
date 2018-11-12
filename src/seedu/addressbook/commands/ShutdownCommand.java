package seedu.addressbook.commands;

/**
 * Terminates the program.
 */

//@@author iamputradanish - reused
public class ShutdownCommand extends Command {

    public static final String COMMAND_WORD = "shutdown";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Exits the program.\n\t"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting PRISM as requested ...";

    @Override
    public CommandResult execute() {
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

}
