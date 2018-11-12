package seedu.addressbook.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.addressbook.password.Password.getUnauthorizedPoCommand;
import static seedu.addressbook.password.Password.isRejectPo;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.CheckCommand;
import seedu.addressbook.commands.CheckPoStatusCommand;
import seedu.addressbook.commands.ClearCommand;
import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.DateTimeCommand;
import seedu.addressbook.commands.DeleteCommand;
import seedu.addressbook.commands.DispatchCommand;
import seedu.addressbook.commands.EditCommand;
import seedu.addressbook.commands.FindCommand;
import seedu.addressbook.commands.HelpCommand;
import seedu.addressbook.commands.IncorrectCommand;
import seedu.addressbook.commands.ListCommand;
import seedu.addressbook.commands.ReadCommand;
import seedu.addressbook.commands.RequestHelpCommand;
import seedu.addressbook.commands.ShowUnreadCommand;
import seedu.addressbook.commands.ShutdownCommand;
import seedu.addressbook.commands.UpdateStatusCommand;
import seedu.addressbook.commands.ViewAllCommand;
import seedu.addressbook.data.exception.IllegalValueException;

import seedu.addressbook.data.person.DateOfBirth;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.PostalCode;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.Status;

public class ParserTest {

    private Parser parser;

    @Before
    public void setup() {
        parser = new Parser();
    }

    @Test
    public void emptyInput_returnsIncorrect() {
        final String[] emptyInputs = { "", "  ", "\n  \n" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, emptyInputs);
    }

    @Test
    public void unknownCommandWord_returnsHelp() {
        final String input = "unknowncommandword arguments arguments";
        parseAndAssertCommandType(input, HelpCommand.class);
    }

    /**
     * Test 0-argument COMMANDS
     */

    @Test
    public void helpCommand_parsedCorrectly() {
        final String input = "help";
        parseAndAssertCommandType(input, HelpCommand.class);
    }

    //@@author ShreyasKp
    @Test
    public void dateTimeCommand_parsedCorrectly() {
        final String input = DateTimeCommand.COMMAND_WORD;
        parseAndAssertCommandType(input, DateTimeCommand.class);
    }

    //@@author

    @Test
    public void clearCommand_parsedCorrectly() {
        final String input = "clear";
        parseAndAssertCommandType(input, ClearCommand.class);
    }

    @Test
    public void listCommand_parsedCorrectly() {
        final String input = "list";
        parseAndAssertCommandType(input, ListCommand.class);
    }

    @Test
    public void exitCommand_parsedCorrectly() {
        final String input = "shutdown";
        parseAndAssertCommandType(input, ShutdownCommand.class);
    }
    //@@author muhdharun
    @Test
    public void checkPoStatusCommand_parsedCorrectly() {
        final String input = "checkstatus";
        parseAndAssertCommandType(input, CheckPoStatusCommand.class);
    }
    /**
     * Test single argument COMMANDS
     */
    @Test
    public void updateStatusCommand_parsedCorrectly() {
        final String input = "updatestatus po1";
        parseAndAssertCommandType(input, UpdateStatusCommand.class);
    }

    @Test
    public void updateStatusCommand_invalidPoArg() {
        final String input = "updatestatus ppp";
        String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateStatusCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, input);
    }

    @Test
    public void updateStatusCommand_noArg() {
        final String input = "updatestatus";
        String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateStatusCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, input);
    }

    //@@author
    /**
     * Test single index argument COMMANDS
     */
    @Test
    public void viewAllCommand_noArgs() {
        final String[] inputs = { "viewall", "viewall " };
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void deleteCommand_noArgs() {
        final String[] inputs = { "delete", "delete " };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewAllCommand_argsIsNotSingleNumber() {
        final String[] inputs = { "viewall notAnumber ", "viewall 8*wh12", "viewall 1 2 3 4 5" };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void viewAllCommand_numericArg_indexParsedCorrectly() {
        final int testIndex = 3;
        final String input = "viewall " + testIndex;
        final ViewAllCommand result = parseAndAssertCommandType(input, ViewAllCommand.class);
        assertEquals(result.getTargetIndex(), testIndex);
    }

    //@@author andyrobert3
    @Test
    public void editCommand_noArgs() {
        final String[] inputs = { "edit", "edit "};
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void editCommand_validArgs_parsedCorrectly() {
        final String[] inputs = {
                // no nric prefix
                String.format("edit $s p/$s s/$s w/$s", Nric.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no postalCode prefix
                String.format("edit n/$s $s s/$s w/$s", Nric.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no status prefix
                String.format("edit n/$s p/$s /$s w/$s", Nric.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no offense(for wantedFor) prefix
                String.format("edit n/$s p/$s s/$s /$s", Nric.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE)
        };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

        for (String input : inputs) {
            parseAndAssertIncorrectWithMessage(resultMessage, input);
        }

    }


    @Test
    public void requestCommand_noArgs() {
        final String[] inputs = { "rb", "rb "};
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequestHelpCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void requestCommand_invalidOffense() {
        final String invalidOffense = "bob";
        final String input = "rb " + invalidOffense;


    }

    @Test
    public void requestCommand_validArgs_parsedCorrectly() {
        final String caseType = "gun";
        final String input = "rb " + caseType;

        final RequestHelpCommand result =
                parseAndAssertCommandType(input, RequestHelpCommand.class);
        assertEquals(caseType, result.getCaseName());
    }

    @Test
    public void dispatchCommand_noArgs() {
        final String[] inputs = { "dispatch", "dispatch "};
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DispatchCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void dispatchCommand_validArgs_parsedCorrectly() {
        final String caseType = "cheating";
        final String backupOfficer = "po1";
        final String requester = "po3";

        final String input = "dispatch " + backupOfficer + " "
            + caseType + " " + requester;
        final DispatchCommand result =
                parseAndAssertCommandType(input, DispatchCommand.class);
        assertEquals(caseType, result.getOffense());
        assertEquals(backupOfficer, result.getBackupOfficer());
        assertEquals(requester, result.getRequester());
    }

    //@@iamputradanish
    @Test
    public void execute_isRejectPo() {
        boolean result = isRejectPo("add");
        assertTrue(result);
    }

    @Test
    public void execute_isRejectPo_allowed() {
        boolean result = isRejectPo("list");
        assertFalse(result);
    }

    @Test
    public void execute_getUnauthorizedPoCommand_getAdd() {
        String result = getUnauthorizedPoCommand("add 1");
        assertEquals("add", result);
    }



    //@@author
    /**
     * Test find persons by keyword in name command
     */

    @Test
    public void findCommand_invalidArgs() {
        // no keywords
        final String[] inputs = {"find", "find "};
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    //@@author ongweekeong

    @Test
    public void showUnreadCommand_parsedCorrectly() {
        final String input = "showunread";
        final ShowUnreadCommand result = parseAndAssertCommandType(input, ShowUnreadCommand.class);
    }

    @Test
    public void readCommand_parsedCorrectly() {
        final int index = 2;
        final String input = "read " + index;
        final ReadCommand result = parseAndAssertCommandType(input, ReadCommand.class);
    }


    @Test
    public void readCommand_invalidArgs_parsedIncorrectly() {
        final String[] indices = {"a", "!", "\'", ".", "#", "$"};
        final String result = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ReadCommand.MESSAGE_USAGE);
        for (String index : indices) {
            String input = "read " + index;
            parseAndAssertIncorrectWithMessage(result, input);
        }
    }

    @Test
    public void readCommand_noArgs_parsedIncorrectly() {
        final String[] inputs = {"read", "read "};
        final String result = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ReadCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(result, inputs);
    }

    //@@author muhdharun -reused
    @Test
    public void findCommand_validArgs_parsedCorrectly() {
        final String keyword = "s1234567a";
        final String input = "find " + keyword;
        final FindCommand result =
                parseAndAssertCommandType(input, FindCommand.class);
        assertEquals(keyword, result.getNric());
    }

    @Test
    public void findCommand_duplicateKeys_parsedCorrectly() {
        final String keyword = "s1234567a";
        // duplicate every keyword
        final String input = "find " + keyword + " " + keyword;
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, input);
    }


    /**
     * Test check persons by nric command
     */
    //@@author muhdharun
    @Test
    public void checkCommand_invalidArgs() {
        // no keywords
        final String[] inputs = {"check", "check "};
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void checkCommand_validArgs_parsedCorrectly() {
        final String keyword = "s1234567a";
        final String input = "check " + keyword;
        final CheckCommand result =
                parseAndAssertCommandType(input, CheckCommand.class);
        assertEquals(keyword, result.getNricKeyword());
    }

    @Test
    public void checkCommand_invalidNricArg() {
        final String keyword = "s12345a";
        final String input = "check " + keyword;
        final String resultMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, input);
    }

    /**
     * Test add person command
     */
    //@@author muhdharun -reused
    @Test
    public void addCommand_invalidArgs() {
        final String[] inputs = {"add", "add ", "add wrong args format",
                // no nric prefix
                String.format("add $s $s d/$s p/$s s/$s w/$s",
                        Name.EXAMPLE, Nric.EXAMPLE,
                        DateOfBirth.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no dateOfBirth prefix
                String.format("add $s n/$s $s p/$s s/$s w/$s",
                        Name.EXAMPLE, Nric.EXAMPLE,
                        DateOfBirth.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no postalCode prefix
                String.format("add $s n/$s d/$s $s s/$s w/$s",
                        Name.EXAMPLE, Nric.EXAMPLE,
                        DateOfBirth.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no status prefix
                String.format("add $s n/$s d/$s p/$s /$s w/$s",
                        Name.EXAMPLE, Nric.EXAMPLE,
                        DateOfBirth.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE),
                // no offense(for wantedFor) prefix
                String.format("add $s n/$s d/$s p/$s s/$s /$s",
                        Name.EXAMPLE, Nric.EXAMPLE,
                        DateOfBirth.EXAMPLE, PostalCode.EXAMPLE,
                        Status.EXAMPLE, Offense.EXAMPLE)
        };
        final String resultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        parseAndAssertIncorrectWithMessage(resultMessage, inputs);
    }

    @Test
    public void addCommand_invalidPersonDataInArgs() {
        final String invalidName = "[]\\[;]";
        final String validName = Name.EXAMPLE;
        final String invalidNricArg = "n/not__numbers";
        final String validNricArg = "n/" + Nric.EXAMPLE;
        final String invalidDateOfBirthArg = "d/1000";
        final String validDateOfBirthArg = "d/" + DateOfBirth.EXAMPLE;
        final String invalidPostalCodeArg = "p/11234565";
        final String validPostalCode = "p/" + PostalCode.EXAMPLE;
        final String invalidStatusArg = "s/not a convict";
        final String validStatusArg = "s/" + Status.EXAMPLE;
        final String invalidWantedForArg = "w/no offence";
        final String validWantedForArg = "w/" + Offense.EXAMPLE;
        final String invalidTagArg = "o/invalid_-[.tag";

        // address can be any string, so no invalid address
        final String addCommandFormatString = "add $s $s $s $s $s $s";

        // test each incorrect person data field argument individually
        final String[] inputs = {
                // invalid name
                String.format(addCommandFormatString, invalidName,
                        validNricArg, validDateOfBirthArg,
                        validPostalCode, validStatusArg, validWantedForArg),
                // invalid nric
                String.format(addCommandFormatString, validName,
                        invalidNricArg, validDateOfBirthArg,
                        validPostalCode, validStatusArg, validWantedForArg),
                // invalid dateOfBirth
                String.format(addCommandFormatString, validName,
                        validNricArg, invalidDateOfBirthArg,
                        validPostalCode, validStatusArg, validWantedForArg),
                // invalid postalCode
                String.format(addCommandFormatString, validName,
                        validNricArg, validDateOfBirthArg,
                        invalidPostalCodeArg, validStatusArg, validWantedForArg),
                // invalid status
                String.format(addCommandFormatString, validName,
                        validNricArg, validDateOfBirthArg, validPostalCode,
                        invalidStatusArg, validWantedForArg),
                // invalid wantedFor
                String.format(addCommandFormatString, validName,
                        validNricArg, validDateOfBirthArg,
                        validPostalCode, validStatusArg, invalidWantedForArg),
                String.format(addCommandFormatString, validName,
                        validNricArg, validDateOfBirthArg, validPostalCode,
                        validStatusArg, validWantedForArg) + " " + invalidTagArg
        };
        for (String input : inputs) {
            parseAndAssertCommandType(input, IncorrectCommand.class);
        }
    }
    //@@author
    @Test
    public void addCommand_validPersonData_parsedCorrectly() {
        final Person testPerson = generateTestPerson();
        final String input = convertPersonToAddCommandString(testPerson);
        final AddCommand result = parseAndAssertCommandType(input, AddCommand.class);
        assertEquals(result.getPerson(), testPerson);
    }

    @Test
    public void addCommand_duplicateTags_merged() {
        final Person testPerson = generateTestPerson();
        String input = convertPersonToAddCommandString(testPerson);
        for (Offense tag : testPerson.getPastOffenses()) {
            // create duplicates by doubling each tag
            input += " o/" + tag.getOffense();
        }

        final AddCommand result = parseAndAssertCommandType(input, AddCommand.class);
        assertEquals(result.getPerson(), testPerson);
    }

    /**
     * @return a test person with the given parameters
     */
    //@@author muhdharun -reused
    private static Person generateTestPerson() {
        try {
            return new Person(
                new Name(Name.EXAMPLE),
                new Nric(Nric.EXAMPLE),
                new DateOfBirth(DateOfBirth.EXAMPLE),
                new PostalCode(PostalCode.EXAMPLE),
                new Status(Status.EXAMPLE),
                new Offense(Offense.EXAMPLE),
                new HashSet<>(Arrays.asList(new Offense("theft"), new Offense("drugs"), new Offense("riot")))
            );
        } catch (IllegalValueException ive) {
            throw new RuntimeException("test person data should be valid by definition");
        }
    }

    /**
     * @return an add command with the person in string form
     */
    private static String convertPersonToAddCommandString(ReadOnlyPerson person) {
        String addCommand = "add "
                + person.getName().fullName
                + " n/" + person.getNric().getIdentificationNumber()
                + " d/" + person.getDateOfBirth().getDob()
                + " p/" + person.getPostalCode().getPostalCode()
                + " s/" + person.getStatus().getCurrentStatus()
                + " w/" + person.getWantedFor().getOffense();
        for (Offense tag : person.getPastOffenses()) {
            addCommand += " o/" + tag.getOffense();
        }
        return addCommand;
    }

    //@@author
    //Utility methods

    /**
     * Asserts that parsing the given inputs will return IncorrectCommand with the given feedback message.
     */
    private void parseAndAssertIncorrectWithMessage(String feedbackMessage, String... inputs) {
        for (String input : inputs) {
            final IncorrectCommand result = parseAndAssertCommandType(input, IncorrectCommand.class);
            assertEquals(result.feedbackToUser, feedbackMessage);
        }
    }

    /**
     * Utility method for parsing input and asserting the class/type of the returned command object.
     *
     * @param input to be parsed
     * @param expectedCommandClass expected class of returned command
     * @return the parsed command object
     */
    private <T extends Command> T parseAndAssertCommandType(String input, Class<T> expectedCommandClass) {
        final Command result = parser.parseCommand(input);
        assertTrue(result.getClass().isAssignableFrom(expectedCommandClass));
        return (T) result;
    }
}
