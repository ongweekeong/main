package seedu.addressbook.parser;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.CheckCommand;
import seedu.addressbook.commands.CheckPoStatusCommand;
import seedu.addressbook.commands.ClearCommand;
import seedu.addressbook.commands.ClearInboxCommand;
import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.DateTimeCommand;
import seedu.addressbook.commands.DeleteCommand;
import seedu.addressbook.commands.DispatchCommand;
import seedu.addressbook.commands.EditCommand;
import seedu.addressbook.commands.FindCommand;
import seedu.addressbook.commands.HelpCommand;
import seedu.addressbook.commands.InboxCommand;
import seedu.addressbook.commands.IncorrectCommand;
import seedu.addressbook.commands.ListCommand;
import seedu.addressbook.commands.LogoutCommand;
import seedu.addressbook.commands.ReadCommand;
import seedu.addressbook.commands.RequestHelpCommand;
import seedu.addressbook.commands.ShowUnreadCommand;
import seedu.addressbook.commands.ShutdownCommand;
import seedu.addressbook.commands.UpdateStatusCommand;
import seedu.addressbook.commands.ViewAllCommand;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.password.Password;

/**
 * Parses user input.
 */
public class Parser {


    //@@author muhdharun -reused
    public static final Pattern PERSON_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<name>[^/]+)"
                    + " n/(?<nric>[^/]+)"
                    + " d/(?<dateOfBirth>[^/]+)"
                    + " p/(?<postalCode>[^/]+)"
                    + " s/(?<status>[^/]+)"
                    + " w/(?<wantedFor>[^/]+)"
                    + "(?<pastOffenseArguments>(?: o/[^/]+)*)"); // variable number of offenses
    //@@author
    
    private static final Pattern PERSON_NRIC_FORMAT = Pattern.compile("(?<nric>[^/]+)");
    private static final String PO_REGEX = "[Pp][Oo][0-9]+";

    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final int INDEX_EDIT_OFFENSE_TAG = 4;

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    private static void setupLogger() {
        setupLoggerForAll(logger);
    }

    /**
     * Logger setup that can be used across all classes to write to the same logger file.
     * @param logger
     */
    public static void setupLoggerForAll(Logger logger) {
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        logger.addHandler(ch);

        try {
            FileHandler fh = new FileHandler("Logger.log", true);
            fh.setLevel(Level.FINE);
            logger.addHandler(fh);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "File logger not working.", ioe);
        }
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        setupLogger();
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        logger.info("Parsed the user input and matching COMMANDS.");

        switch (commandWord) {
        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case DateTimeCommand.COMMAND_WORD:
            return new DateTimeCommand();

        case DeleteCommand.COMMAND_WORD:
            return prepareDelete(arguments);

        case EditCommand.COMMAND_WORD:
            return prepareEdit(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case CheckCommand.COMMAND_WORD:
            return prepareCheck(arguments);

        case CheckPoStatusCommand.COMMAND_WORD:
            return new CheckPoStatusCommand();

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case ShowUnreadCommand.COMMAND_WORD:
            return new ShowUnreadCommand();

        case InboxCommand.COMMAND_WORD:
            return new InboxCommand();

        case ClearInboxCommand.COMMAND_WORD:
            return new ClearInboxCommand();

        case UpdateStatusCommand.COMMAND_WORD:
            return prepareUpdateStatus(arguments);

        case ReadCommand.COMMAND_WORD:
            return prepareRead(arguments);

        case ViewAllCommand.COMMAND_WORD:
            return prepareViewAll(arguments);

        case RequestHelpCommand.COMMAND_WORD:
            return prepareRequest(arguments);

        case DispatchCommand.COMMAND_WORD:
            return prepareDispatch(arguments);

        case ShutdownCommand.COMMAND_WORD:
            return new ShutdownCommand();

        case LogoutCommand.COMMAND_WORD:
            return new LogoutCommand();

        case HelpCommand.COMMAND_WORD: // Fallthrough
        default:
            return new HelpCommand();
        }
    }
    //@@author muhdharun -reused
    /**
     * Parses arguments in the context of the add person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            return new AddCommand(
                    matcher.group("name"),
                    matcher.group("nric"),
                    matcher.group("dateOfBirth"),
                    matcher.group("postalCode"),
                    matcher.group("status"),
                    matcher.group("wantedFor"),
                    getTagsFromArgs(matcher.group("pastOffenseArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    //@@author
<<<<<<< HEAD
=======


>>>>>>> 374b2bdf29856462f5754e086c059f5168bed080
    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" o/", "").split(" o/"));
        return new HashSet<>(tagStrings);
    }


    //@@author muhdharun -reused

    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        try {
            final String nric = parseArgsAsNric(args);

            return new DeleteCommand(new Nric(nric));
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Invalid delete command format.", e);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        } catch (IllegalValueException ive) {
            logger.log(Level.WARNING, "Invalid name/id inputted.", ive);
            return new IncorrectCommand(ive.getMessage());
        }
    }


    //@@author andyrobert3

    /**
     * Parses user input for Edit command into string of offenses
     * @param argParts
     * @param editCommandIdentifiers
     * @return String of offenses separated by space
     * @throws IllegalValueException
     */
    private String addOffensesToString(String[] argParts, String[] editCommandIdentifiers)
            throws IllegalValueException {
        StringBuilder offenseString = new StringBuilder();

        for (String argument : argParts) {
            if (argument.length() <= 2 || !Arrays.asList(editCommandIdentifiers).contains(argument.substring(0, 2))) {
                logger.warning("User input is invalid for Edit Command");
                throw new IllegalValueException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        EditCommand.MESSAGE_USAGE));
            }

            for (int i = 0; i < editCommandIdentifiers.length; i++) {
                if (argument.substring(0, 2).equals(editCommandIdentifiers[i])) {
                    if (editCommandIdentifiers[i].equals(editCommandIdentifiers[INDEX_EDIT_OFFENSE_TAG])) {
                        offenseString.append(" ").append(argument);
                    }
                }
            }
        }

        return offenseString.toString();
    }

    /**
     * Helper method to parse user input to Edit Command Formats
     * @param argParts
     * @param editCommandIdentifiers
     * @return Array of Strings with relevant parameters
     */
    private String[] parseEditInputParameters(String[] argParts, String[] editCommandIdentifiers) {
        String[] userInputParameters = new String[editCommandIdentifiers.length];
        for (String argument : argParts) {
            for (int i = 0; i < editCommandIdentifiers.length; i++) {
                if (argument.substring(0, 2).equals(editCommandIdentifiers[i])) {
                    userInputParameters[i] = argument.substring(2);
                    break;
                }
            }
        }

        return userInputParameters;
    }

    /**
     * Parses arguments in the context of the edit person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEdit(String args) {
        final int indexNricTag = 0;
        args = args.trim();

        String[] argParts = args.split("\\s");
        String[] editCommandIdentifiers = {
            "n/", "p/", "s/", "w/", "o/"
        };

        try {
            String offenseString = addOffensesToString(argParts, editCommandIdentifiers);
            String[] userInputParameters = parseEditInputParameters(argParts, editCommandIdentifiers);

            userInputParameters[INDEX_EDIT_OFFENSE_TAG] = (!offenseString.equals(""))
                    ? offenseString : null;

            Set<String> offenses = null;

            if (userInputParameters[indexNricTag] == null) {
                logger.warning("User did not enter NRIC for Edit Command");
                throw new IllegalValueException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        EditCommand.MESSAGE_USAGE));
            }

            if (userInputParameters[INDEX_EDIT_OFFENSE_TAG] != null) {
                offenses = getTagsFromArgs(userInputParameters[INDEX_EDIT_OFFENSE_TAG]);
            }

            return new EditCommand(
                    userInputParameters[0],
                    userInputParameters[1],
                    userInputParameters[2],
                    userInputParameters[3],
                    offenses
            );

        } catch (IllegalValueException ive) {
            logger.log(Level.WARNING, "Invalid edit command format.", ive);
            return new IncorrectCommand(ive.getMessage());
        }
    }

    //@@author ongweekeong

    /**
     *
     * @param args
     * @return
     */
    private Command prepareRead(String args) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ReadCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid read command format.", e);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ReadCommand.MESSAGE_USAGE));
        }
    }

    //@@author
    /**
     * Parses arguments in the context of the view all command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareViewAll(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewAllCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAllCommand.MESSAGE_USAGE));
        }
    }

    //@@author
    /**
     * Parses the given arguments string as a single index number.
     *
     * @param args arguments string to parse as index number
     * @return the parsed index number
     * @throws ParseException if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            logger.warning("Index number does not exist in argument");
            throw new ParseException("Could not find index number to parse");
        }
        try {
            return Integer.parseInt(matcher.group("targetIndex"));
        } catch (NumberFormatException nfe) {
            new BigInteger(matcher.group("targetIndex")); //Throws NFE if input index is not numeric.
            return Integer.MAX_VALUE;
        }
    }
    //@@author muhdharun
    /**
     * Parses argument string as an Nric
     *
     * @param args argument string to parse as Nric
     * @return the prepared Nric
     */

    private String parseArgsAsNric(String args) throws ParseException {
        final Matcher matcher = PERSON_NRIC_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            logger.warning("Nric does not exist in argument");
            throw new ParseException("Could not find Nric to parse");
        }
        return matcher.group(0);
    }

    /**
     * Parses arguments in the context of the check or find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */

    private Command prepareCheck(String args) {
        args = args.trim();
        if (Nric.isValidNric(args)) {
            return new CheckCommand(args);
        } else {
            logger.warning("Nric argument is invalid");
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckCommand.MESSAGE_USAGE));
        }
    }

    /**
<<<<<<< HEAD
     * Parses arguments in the context of the find person command.
     * @param args
     * @return
=======
     * @param args full command args string
     * @return the prepared command
>>>>>>> 374b2bdf29856462f5754e086c059f5168bed080
     */
    private Command prepareFind(String args) {
        args = args.trim();
        if (Nric.isValidNric(args)) {
            return new FindCommand(args);
        } else {
            logger.warning("Nric argument is invalid");
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of updating a PO's status.
     *
     * @param args full command args string
     * @return the prepared command
     */

    private Command prepareUpdateStatus(String args) {
        args = args.trim();
        if (args.matches(PO_REGEX)) {
            return new UpdateStatusCommand(args);
        } else {
            logger.warning("an existing or valid PO must be stated");
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UpdateStatusCommand.MESSAGE_USAGE));
        }

    }

    //@@author andyrobert3

    /**
     * Parses arguments in context of request help command.
     *
     * @param args full command args string
     * @return the prepared request command
     */
    private Command prepareRequest(String args) {
        String message;
        String caseName = args.trim();

        if (caseName.length() == 0) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RequestHelpCommand.MESSAGE_USAGE));
        }

        message = Password.getId().toUpperCase() + " needs help with " + caseName + " at location "
                + PatrolResourceStatus.getLocation(Password.getId()).getGoogleMapsUrl();

        try {
            return new RequestHelpCommand(caseName, message);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(Offense.MESSAGE_OFFENSE_INVALID + Offense.getListOfValidOffences());
        }
    }

    //@@author andyrobert3
    /**
     * Parses arguments in context of dispatch command
     *
     * @param args full command args string
     * @return the prepared request command
     */
    private Command prepareDispatch(String args) {
        String backupOfficer;
        String dispatchRequester;
        String caseName;
        String[] argParts = args.trim().split("\\s+", 3);

        if (argParts.length < 3) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DispatchCommand.MESSAGE_USAGE));
        }

        backupOfficer = argParts[0].toLowerCase();
        caseName = argParts[1].toLowerCase();
        dispatchRequester = argParts[2].toLowerCase();

        try {
            if (backupOfficer.equalsIgnoreCase(dispatchRequester)) {
                throw new IllegalValueException(String.format(DispatchCommand.getMessageBackupDispatchSame(),
                        backupOfficer));
            }
            return new DispatchCommand(backupOfficer, dispatchRequester, caseName);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }


    }

}
