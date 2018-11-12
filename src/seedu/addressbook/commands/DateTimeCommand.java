//@@author ShreyasKp
package seedu.addressbook.commands;

import static seedu.addressbook.parser.Parser.setupLoggerForAll;

import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.addressbook.timeanddate.TimeAndDate;

/**
 * Command to display current date and time on screen
 */

public class DateTimeCommand extends Command {

    public static final String COMMAND_WORD = "time";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Returns current date and time \n\t"
            + "Example: " + COMMAND_WORD;

    private static final Logger logger = Logger.getLogger(DateTimeCommand.class.getName());

    private TimeAndDate timeAndDate;

    public DateTimeCommand() {
        timeAndDate = new TimeAndDate();
    }
    private static void setupLogger() {
        setupLoggerForAll(logger);
    }

    @Override
    public CommandResult execute() {
        setupLogger();
        logger.log(Level.INFO, "Time command works");
        return new CommandResult(timeAndDate.outputDatMainHrs());
    }
}
