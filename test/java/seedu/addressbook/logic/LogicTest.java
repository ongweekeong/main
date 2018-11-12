package seedu.addressbook.logic;

import static java.lang.Math.abs;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static seedu.addressbook.commands.Command.findPrediction;
import static seedu.addressbook.common.Messages.MESSAGE_FILE_NOT_FOUND;
import static seedu.addressbook.common.Messages.MESSAGE_INBOX_FILE_NOT_FOUND;
import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.addressbook.common.Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
import static seedu.addressbook.common.Messages.MESSAGE_TIMESTAMPS_LISTED_OVERVIEW;
import static seedu.addressbook.password.Password.MESSAGE_ENTER_COMMAND;
import static seedu.addressbook.password.Password.MESSAGE_ENTER_PASSWORD;
import static seedu.addressbook.password.Password.MESSAGE_HQP;
import static seedu.addressbook.password.Password.MESSAGE_PASSWORD_LENGTH;
import static seedu.addressbook.password.Password.MESSAGE_PASSWORD_MINIMUM_LENGTH;
import static seedu.addressbook.password.Password.MESSAGE_TRY_AGAIN;
import static seedu.addressbook.password.Password.MESSAGE_VALID;
import static seedu.addressbook.password.Password.correctHqp;
import static seedu.addressbook.password.Password.correctPO1;
import static seedu.addressbook.password.Password.correctPO2;
import static seedu.addressbook.password.Password.correctPO3;
import static seedu.addressbook.password.Password.correctPO4;
import static seedu.addressbook.password.Password.correctPO5;
import static seedu.addressbook.password.Password.getFullId;
import static seedu.addressbook.password.Password.getId;
import static seedu.addressbook.password.Password.getIsUpdatingPassword;
import static seedu.addressbook.password.Password.isUpdatePasswordConfirmNow;

import static seedu.addressbook.password.Password.unlockHqp;
import static seedu.addressbook.password.Password.unlockPo;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.autocorrect.AutoCorrect;
import seedu.addressbook.autocorrect.CheckDistance;
import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.CheckCommand;
import seedu.addressbook.commands.CheckPoStatusCommand;
import seedu.addressbook.commands.ClearCommand;
import seedu.addressbook.commands.ClearInboxCommand;
import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.commands.DateTimeCommand;
import seedu.addressbook.commands.DeleteCommand;
import seedu.addressbook.commands.Dictionary;
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
import seedu.addressbook.common.HttpRestClient;
import seedu.addressbook.common.Location;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.PatrolResourceUnavailableException;
import seedu.addressbook.data.person.DateOfBirth;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.PostalCode;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.Status;
import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.inbox.NotificationReader;
import seedu.addressbook.inbox.NotificationWriter;
import seedu.addressbook.password.Password;
import seedu.addressbook.storage.StorageFile;
import seedu.addressbook.timeanddate.TimeAndDate;
import seedu.addressbook.ui.UiFormatter;

public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private StorageFile saveFile;
    private AddressBook addressBook;
    private Logic logic;

    @Before
    public void setup() throws Exception {
        saveFile = new StorageFile(saveFolder.newFile("testSaveFile.txt").getPath());
        addressBook = new AddressBook();
        saveFile.save(addressBook);
        logic = new Logic(saveFile, addressBook);
    }

    @Test
    public void constructor() {
        //Constructor is called in the setup() method which executes before every test, no need to call it here again.

        //Confirm the last shown list is empty
        assertEquals(Collections.emptyList(), logic.getLastShownList());
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Both the 'address book' and the 'last shown list' are expected to be empty.
     * @see #assertCommandBehavior(String, String, AddressBook, boolean, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, AddressBook.empty(), false, Collections.emptyList());
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedAddressBook} <br>
     *      - the internal 'last shown list' matches the {@code expectedLastList} <br>
     *      - the storage file content matches data in {@code expectedAddressBook} <br>
     */
    private void assertCommandBehavior(String inputCommand,
                                      String expectedMessage,
                                      AddressBook expectedAddressBook,
                                      boolean isRelevantPersonsExpected,
                                      List<? extends ReadOnlyPerson> lastShownList) throws Exception {

        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedMessage, r.feedbackToUser);
        assertEquals(r.getRelevantPersons().isPresent(), isRelevantPersonsExpected);
        if (isRelevantPersonsExpected) {
            assertEquals(lastShownList, r.getRelevantPersons().get());
        }

        //Confirm the state of data is as expected
        assertEquals(expectedAddressBook, addressBook);
        assertEquals(lastShownList, logic.getLastShownList());
        assertEquals(addressBook, saveFile.load());
    }

    //@@author ongweekeong
    /**
     * Checks that message generated is the message that is read.
     * First checks that timestamp is the same. If it is, then check the rest of the message.
     * @param inputCommand
     * @param expectedResult
     * @param testMsg
     * @param msgIndex
     * @return
     * @throws Exception
     */
    private String assertCommandBehavior(String inputCommand, String expectedResult,
                                         Msg testMsg, int msgIndex) throws Exception {
        CommandResult r = logic.execute(inputCommand);
        String inputTime = parseMsgForTimestamp(r.feedbackToUser);
        String expectedTime = parseMsgForTimestamp(testMsg.getTimeString());

        assertEqualsTimestamp(inputTime, expectedTime, 500);

        testMsg.setTime(adjustExpectedTimestamp(r.feedbackToUser, msgIndex));

        expectedResult += InboxCommand.concatenateMsg(msgIndex, testMsg);

        assertEquals(String.format(expectedResult, msgIndex), r.feedbackToUser);
        return expectedResult;
    }

    private void assertCommandBehavior(String commandWord, String expectedResult, Msg testMsg) throws Exception {
        CommandResult r = logic.execute(commandWord);
        expectedResult += InboxCommand.concatenateMsg(1, testMsg);
        assertEquals(r.feedbackToUser, expectedResult);
    }
    private void assertCommandBehavior(String commandWord, String expectedResult, String testMsg) throws Exception {
        CommandResult r = logic.execute(commandWord);
        assertEquals(r.feedbackToUser, expectedResult);
    }

    /**
     * Executes the command and confirms that the result message (Timestamp)
     * is correct (within the given tolerance threshold).
     * @param inputCommand
     * @param expectedMessage
     * @throws Exception
     */
    private void assertTimeCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        CommandResult r = logic.execute(inputCommand);
        String[] parts = r.feedbackToUser.split(" ", 2);
        parts[1] = parts[1].substring(0, 4);
        String[] expected = expectedMessage.split("-", 2);
        String[] expectedTime = expected[1].split(":", 3);
        String expectedTimeFormatted = expectedTime[0] + expectedTime[1];

        assertEquals(parts[0], expected[0]);
        assertEquals(parts[1], expectedTimeFormatted);
        expectedMessage = r.feedbackToUser;

        assertEquals(expectedMessage, r.feedbackToUser);
    }

    /**
     * Checks that expected timestamp and actual timestamp are within a tolerance boundary.
     * If it is, they are considered equal.
     * @param time1
     * @param time2
     * @param tolerance
     */
    private void assertEqualsTimestamp(String time1, String time2, int tolerance) {
        String[] inputTime = time1.split(":", 4);
        String[] expectedTime = time2.split(":", 4);
        inputTime[3] = inputTime[2] + inputTime[3].substring(0, 3);
        expectedTime[3] = expectedTime[2] + expectedTime[3].substring(0, 3);
        int difference = abs(Integer.parseInt(inputTime[3]) - Integer.parseInt(expectedTime[3]));
        if (inputTime[0].equals(expectedTime[0]) && inputTime[1].equals(expectedTime[1])
                && inputTime[2].equals(expectedTime[2]) && (
                difference <= tolerance)) {
            time2 = time1;
        }
        assertEquals(time1, time2);
    }

    //@@author ShreyasKp
    /**
     * Executes the autocorrection algorithm and confirms that the invalid input has a prediction
     * @param expected The expected output
     * @param inputs The invalid command inputs
     */
    private void assertCommandBehaviourAutocorrect(String expected, String[] inputs) throws Exception {
        CheckDistance checker = new CheckDistance();
        AutoCorrect correction = new AutoCorrect();
        Dictionary dict = new Dictionary();

        Password.unlockHqp();

        expected = expected.substring(expected.indexOf("!") + 1);
        for (String input: inputs) {
            String output = AutoCorrect.getCommand(input);
            String command = checker.checkDistance(output);
            String displayCommand = correction.getResultOfInvalidCommand(output);
            assertEquals(String.format(dict.getCommandErrorMessage(), command) + "\n"
                    + expected, displayCommand);
        }

        Password.lockIsHqp();
    }

    /**
     * Executes the autocorrection algorithm and confirms that the invalid input does not have a prediction
     * @param expected The expected output
     * @param inputs The invalid command inputs
     */
    private void assertCommandBehaviourAutocorrectInvalid(String expected, String[] inputs) throws Exception {
        CheckDistance checker = new CheckDistance();
        AutoCorrect correction = new AutoCorrect();
        Dictionary dict = new Dictionary();
        boolean isHqp = true;

        for (String input: inputs) {
            String output = checker.checkDistance(input);
            String suggestion = String.format(dict.getCommandErrorMessage(), output);
            String displayCommand = correction.checkCommand(input, isHqp);
            assertEquals(expected, displayCommand);
        }
    }

    /**
     * Creates people in the addressbook
     * @return List of people to be added to the addressbook
     * @throws Exception
     */
    private List<Person> generateAddressbookForAutocorrect() throws Exception {
        //add people into addressbook
        TestDataHelper helper = new TestDataHelper();
        Person firstPerson = helper.generatePerson(1);
        Person secondPerson = helper.adam();

        return helper.generatePersonList(firstPerson, secondPerson);

    }
    //@@author iamputradanish
    @Test
    public void execute_unknownCommandWord_forHqp() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        Password.unlockHqp();
        assertCommandBehavior(unknownCommand, HelpCommand.MESSAGE_ALL_USAGES);
        Password.lockIsHqp();
    }

    @Test
    public void execute_help_forHqp() throws Exception {
        Password.unlockHqp();
        assertCommandBehavior("help", HelpCommand.MESSAGE_ALL_USAGES);
        Password.lockIsHqp();
    }

    @Test
    public void execute_unknownCommandWord_forPo() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        unlockPo();
        assertCommandBehavior(unknownCommand, HelpCommand.MESSAGE_PO_USAGES);
        Password.lockIsPo();
    }

    @Test
    public void execute_help_forPo() throws Exception {
        unlockPo();
        assertCommandBehavior("help", HelpCommand.MESSAGE_PO_USAGES);
        Password.lockIsPo();
    }

    @Test
    public void execute_logout() throws Exception {
        assertCommandBehavior(LogoutCommand.COMMAND_WORD, LogoutCommand.MESSAGE_LOCK);
    }

    //@@iamputradanish - reused
    @Test
    public void execute_shutdown() throws Exception {
        assertCommandBehavior(ShutdownCommand.COMMAND_WORD, ShutdownCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    //@author
    @Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        addressBook.addPerson(helper.generatePerson(1));
        addressBook.addPerson(helper.generatePerson(2));
        addressBook.addPerson(helper.generatePerson(3));

        assertCommandBehavior(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_SUCCESS, AddressBook.empty(),
                false, Collections.emptyList());
    }

    //@@author muhdharun -reused
    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "add wrong args wrong args wrong args", expectedMessage);
        assertCommandBehavior(
                "add Valid Name s1234567a d/1980 p/123456 s/clear w/none", expectedMessage);
        assertCommandBehavior(
                "add Valid Name n/s1234567a 1980 p/123456 s/clear w/none", expectedMessage);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 123456 s/clear w/none", expectedMessage);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 p/123456 clear w/none", expectedMessage);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 123456 s/clear none", expectedMessage);
    }

    @Test
    public void execute_add_invalidPersonData() throws Exception {
        assertCommandBehavior(
                "add []\\[;] n/s1234567a d/1980 p/123456 s/clear w/none", Name.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "add Valid Name n/s123457a d/1980 p/123456 s/clear w/none", Nric.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/188 p/123456 s/clear w/none",
                DateOfBirth.MESSAGE_DATE_OF_BIRTH_CONSTRAINTS);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 p/13456 s/clear w/none",
                PostalCode.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 p/123456 s/xc w/none o/rob",
                String.format(Offense.MESSAGE_OFFENSE_INVALID + "\n" + Offense.getListOfValidOffences(), "rob"));
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 p/123456 s/wanted w/none o/none", Person.getWantedForWarning());
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/1980 p/123456 s/wanted w/none o/none", Person.getWantedForWarning());
        assertCommandBehavior(
                "add Valid Name n/s1234567a d/2099 p/123456 s/wanted w/none o/none",
                DateOfBirth.MESSAGE_DATE_OF_BIRTH_CONSTRAINTS);
    }

    //@@author muhdharun
    @Test
    public void execute_copyConstructorPerson() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        ReadOnlyPerson test = helper.adam();
        Person duplicateAdam = new Person(test);
        assertEquals(test, duplicateAdam);
    }

    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Person toBeAdded = helper.adam();
        AddressBook expectedAb = new AddressBook();
        expectedAb.addPerson(toBeAdded);

        AddCommand toAdd = new AddCommand(toBeAdded);
        assertEquals(toBeAdded, toAdd.getPerson());

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAb,
                false,
                Collections.emptyList());

    }

    //@@author
    @Test
    public void execute_addDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Person toBeAdded = helper.adam();
        AddressBook expectedAb = new AddressBook();
        expectedAb.addPerson(toBeAdded);

        // setup starting state
        addressBook.addPerson(toBeAdded); // person already in internal address book

        // execute command and verify result
        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                AddCommand.MESSAGE_DUPLICATE_PERSON,
                expectedAb,
                false,
                Collections.emptyList());

    }

    @Test
    public void execute_list_showsAllPersons() throws Exception {
        // prepare expectations
        TestDataHelper helper = new TestDataHelper();
        AddressBook expectedAb = helper.generateAddressBook(false, false);
        List<? extends ReadOnlyPerson> expectedList = expectedAb.getAllPersons().immutableListView();

        // prepare address book state

        helper.addToAddressBook(addressBook, false, false);
        assertCommandBehavior("list",
                              Command.getMessageForPersonListShownSummary(expectedList),
                              expectedAb,
                              true,
                              expectedList);
    }


    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single person in the last shown list, using visible index.
     * @param commandWord to test assuming it targets a single person in the last shown list based on visible index.
     */
    private void assertInvalidIndexBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
        TestDataHelper helper = new TestDataHelper();
        List<Person> lastShownList = helper.generatePersonList(false, true);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior(commandWord + " -1", expectedMessage, AddressBook.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 0", expectedMessage, AddressBook.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 3", expectedMessage, AddressBook.empty(), false, lastShownList);

    }


    /**
     * Generates a person list.
     * @param commandWord
     * @throws Exception
     */
    private void assertInvalidCommandFormatBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = Nric.MESSAGE_NAME_CONSTRAINTS;
        TestDataHelper helper = new TestDataHelper();
        List<Person> lastShownList = helper.generatePersonList(false, true);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior(commandWord + " -1", expectedMessage, AddressBook.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 0", expectedMessage, AddressBook.empty(), false, lastShownList);
        assertCommandBehavior(commandWord + " 3", expectedMessage, AddressBook.empty(), false, lastShownList);

    }

    //@@author andyrobert3
    @Test
    public void execute_location_setters() {
        Location location = new Location(1.2345, 2.4567);
        double newLatitude = 4.5679;
        double newLongitude = 9.6533;

        location.setLatitude(newLatitude);
        location.setLongitude(newLongitude);

        assertTrue(location.getLatitude() == newLatitude);
        assertTrue(location.getLongitude() == newLongitude);
    }

    @Test
    public void execute_request_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequestHelpCommand.MESSAGE_USAGE);
        assertCommandBehavior("rb", expectedMessage);
        assertCommandBehavior("rb    ", expectedMessage);
    }

    @Test
    public void execute_request_invalidOffense() throws Exception {
        String expectedMessage = Offense.MESSAGE_OFFENSE_INVALID + Offense.getListOfValidOffences();
        assertCommandBehavior(RequestHelpCommand.COMMAND_WORD + " crime", expectedMessage);
        assertCommandBehavior(RequestHelpCommand.COMMAND_WORD + " tired", expectedMessage);
    }


    @Test
    public void execute_request_successful() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_HQP_INBOX);
        String expectedMessage = String.format(RequestHelpCommand.getMessageRequestSuccess(), "hqp");
        Password.unlockHqp();

        assertCommandBehavior(RequestHelpCommand.COMMAND_WORD + " gun", expectedMessage);
        assertCommandBehavior(RequestHelpCommand.COMMAND_WORD + " theft", expectedMessage);
        assertCommandBehavior(RequestHelpCommand.COMMAND_WORD + " riot", expectedMessage);
    }

    @Test
    public void execute_requestSuccessful_checkMsg() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_HQP_INBOX);
        Password.unlockHqp();
        Password.lockIsPo();
        logic.execute(RequestHelpCommand.COMMAND_WORD + " gun");
        String expectedUnreadMessagesResult = String.format(Messages.MESSAGE_UNREAD_MSG_NOTIFICATION, 1) + "\n";
        assertCommandBehavior(ShowUnreadCommand.COMMAND_WORD, expectedUnreadMessagesResult,
                RequestHelpCommand.getRecentMsg());
        Password.lockIsHqp();
    }



    @Test
    public void execute_dispatch_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DispatchCommand.MESSAGE_USAGE);
        assertCommandBehavior(DispatchCommand.COMMAND_WORD, expectedMessage);
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po1", expectedMessage);
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po1 gun", expectedMessage);
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + "      ", expectedMessage);
        assertCommandBehavior(DispatchCommand.COMMAND_WORD, expectedMessage);

    }

    @Test
    public void execute_dispatch_invalidOffense() throws Exception {
        assertCommandBehavior("dispatch po1 help po2", String.format(Offense.MESSAGE_OFFENSE_INVALID + "\n"
                + Offense.getListOfValidOffences(), "help"));
        assertCommandBehavior("dispatch po4 backup po1", String.format(Offense.MESSAGE_OFFENSE_INVALID + "\n"
                + Offense.getListOfValidOffences(), "backup"));
    }

    @Test
    public void execute_dispatch_successful() throws Exception {
        PatrolResourceStatus.resetPatrolResourceStatus();
        NotificationWriter.clearAllInbox();
        String expectedMessage1 = String.format(DispatchCommand.getMessageDispatchSuccess(), "po5");
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po1 gun po5", expectedMessage1);

        PatrolResourceStatus.resetPatrolResourceStatus();
        NotificationWriter.clearAllInbox();
        String expectedMessage2 = String.format(DispatchCommand.getMessageDispatchSuccess(), "po4");
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po3 gun po4", expectedMessage2);
    }

    @Test
    public void execute_dispatch_engagedOfficer() throws Exception {
        PatrolResourceStatus.setStatus("po3", true);
        String baseMessage = "Patrol resource %s is engaged.\n" + DispatchCommand.getMessageOfficerUnavailable();

        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " hqp theft po3", String.format(baseMessage, "hqp"));
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po3 riot po2", String.format(baseMessage, "po3"));
    }

    @Test
    public void execute_dispatch_backupRequesterSameOfficer() throws Exception {
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po1 gun po1",
                String.format(DispatchCommand.getMessageBackupDispatchSame(), "po1"));
        assertCommandBehavior(DispatchCommand.COMMAND_WORD + " po5 gun po5",
                String.format(DispatchCommand.getMessageBackupDispatchSame(), "po5"));
    }

    @Test
    public void execute_patrolResource_getDefaultDetails() {
        PatrolResourceStatus.resetPatrolResourceStatus();
        assertEquals(PatrolResourceStatus.getPatrolResource(" "), PatrolResourceStatus.getPatrolResource("hqp"));
    }

    @Test
    public void execute_httpGetRequest_internetAvailable() throws Exception {
        String testUrl = "https://www.google.com";

        HttpRestClient httpRestClient = new HttpRestClient();
        int statusCode = httpRestClient.requestGetResponse(testUrl)
                            .getStatusLine().getStatusCode();

        assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 204);
    }

    @Test
    public void execute_request_recentMessageFail() {
        RequestHelpCommand.resetRecentMessage();
        thrown.expect(NullPointerException.class);
        RequestHelpCommand.getRecentMsg();
    }

    @Test
    public void execute_request_invalidPatrolResourceId() throws Exception {
        assertCommandBehavior("rb cheating", Messages.MESSAGE_PO_NOT_FOUND);
        assertCommandBehavior("rb gun", Messages.MESSAGE_PO_NOT_FOUND);
    }

    //@@author
    @Test
    public void execute_viewAll_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE);
        assertCommandBehavior("viewall ", expectedMessage);
        assertCommandBehavior("viewall arg not number", expectedMessage);
    }

    @Test
    public void execute_viewAll_invalidIndex() throws Exception {
        assertInvalidIndexBehaviorForCommand("viewall");
    }

    @Test
    public void execute_viewAll_alsoShowsPrivate() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1);
        Person p2 = helper.generatePerson(2);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);
        AddressBook expectedAb = helper.generateAddressBook(lastShownList);
        helper.addToAddressBook(addressBook, lastShownList);

        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 1",
                            String.format(ViewAllCommand.MESSAGE_VIEW_PERSON_DETAILS, p1.getAsTextShowAll()),
                            expectedAb,
                            false,
                            lastShownList);

        assertCommandBehavior("viewall 2",
                            String.format(ViewAllCommand.MESSAGE_VIEW_PERSON_DETAILS, p2.getAsTextShowAll()),
                            expectedAb,
                            false,
                            lastShownList);
    }

    @Test
    public void execute_tryToViewAllPersonMissingInAddressBook_errorMessage() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1);
        Person p2 = helper.generatePerson(2);
        List<Person> lastShownList = helper.generatePersonList(p1, p2);

        AddressBook expectedAb = new AddressBook();
        expectedAb.addPerson(p1);

        addressBook.addPerson(p1);
        logic.setLastShownList(lastShownList);

        assertCommandBehavior("viewall 2",
                                Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                                expectedAb,
                                false,
                                lastShownList);
    }

    @Test
    public void execute_delete_invalidArgsFormat() throws Exception {
        String expectedMessage1 = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        String expectedMessage2 = Nric.MESSAGE_NAME_CONSTRAINTS;
        assertCommandBehavior("delete ", expectedMessage1);
        assertCommandBehavior("delete arg not number", expectedMessage2);
    }

    @Test
    public void execute_delete_invalidCommandFormat() throws Exception {
        assertInvalidCommandFormatBehaviorForCommand("delete");
    }

    //@@author muhdharun -reused
    @Test
    public void execute_delete_removesCorrectPerson() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1);
        Person p2 = helper.generatePerson(2);
        Person p3 = helper.generatePerson(3);

        List<Person> threePersons = helper.generatePersonList(p1, p2, p3);

        AddressBook expectedAb = helper.generateAddressBook(threePersons);
        expectedAb.removePerson(p2);


        helper.addToAddressBook(addressBook, threePersons);
        logic.setLastShownList(threePersons);

        assertCommandBehavior("delete g9999992t",
                                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, p2),
                                expectedAb,
                                false,
                                threePersons);
    }

    @Test
    public void execute_delete_missingInAddressBook() throws Exception {

        TestDataHelper helper = new TestDataHelper();
        Person p1 = helper.generatePerson(1);
        Person p2 = helper.generatePerson(2);
        Person p3 = helper.generatePerson(3);

        List<Person> threePersons = helper.generatePersonList(p1, p2, p3);

        AddressBook expectedAb = helper.generateAddressBook(threePersons);
        expectedAb.removePerson(p2);

        helper.addToAddressBook(addressBook, threePersons);
        addressBook.removePerson(p2);
        logic.setLastShownList(threePersons);

        assertCommandBehavior("delete g9999992t",
                                Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                                expectedAb,
                                false,
                                threePersons);
    }

    //@@author andyrobert3
    @Test
    public void execute_edit_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertCommandBehavior("edit ", expectedMessage);
        assertCommandBehavior("edit hello world", expectedMessage);
    }

    //@@author andyrobert3
    @Test
    public void execute_edit_invalidDataFormat() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person adam = helper.generatePersonWithNric("s1234567a");
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(adam);

        assertCommandBehavior("edit n/s1234567a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        assertCommandBehavior(
                "edit n/s123456a p/510247", Nric.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "edit n/s1234567a p/50247 w/murder o/gun", PostalCode.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "edit n/s1234567a p/123456 s/c w/none", Status.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior(
                "edit n/s1234567a p/133456 s/wanted w/ne", String.format(Offense.MESSAGE_OFFENSE_INVALID
                        + "\n" + Offense.getListOfValidOffences(), "ne"));
        assertCommandBehavior(
                "edit n/s1234567a p/134546 s/xc w/none o/rr", String.format(Offense.MESSAGE_OFFENSE_INVALID
                        + "\n" + Offense.getListOfValidOffences(), "rr"));
    }

    @Test
    public void execute_edit_successful() throws Exception {
        String nric = "s1234567a";

        TestDataHelper helper = new TestDataHelper();
        Person toBeEdited = helper.generatePersonWithNric(nric);
        addressBook.addPerson(toBeEdited);

        assertCommandBehavior("edit n/s1234567a p/444555 s/xc w/theft o/theft",
                                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, nric),
                                addressBook,
                                false,
                                Collections.emptyList());
        assertCommandBehavior("edit n/s1234567a o/riot",
                                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, nric),
                                addressBook,
                                false,
                                Collections.emptyList());
        assertCommandBehavior("edit n/s1234567a p/123456 w/gun",
                                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, nric),
                                addressBook,
                                false,
                                Collections.emptyList());
    }

    @Test
    public void execute_edit_personNotFound() throws Exception {
        assertCommandBehavior("edit n/s1234567a p/444555 s/clear w/none o/none",
                                Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                                addressBook,
                                false,
                                addressBook.getAllPersons().immutableListView());

        assertCommandBehavior("edit n/f3456789b p/444555 s/xc w/drugs o/drugs",
                                Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK,
                                addressBook,
                                false,
                                Collections.emptyList());

    }

    //@@author muhdharun -reused
    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }

    //@@author muhdharun
    /*
    @Test
    public void execute_find_onlyMatchesFullNric() throws Exception {
        TestDataHelper helper = new TestDataHelper();

        Person pTarget1 = helper.generatePersonWithNric("s1234567a");
        Person pTarget2 = helper.generatePersonWithNric("s1234567b");
        Person p1 = helper.generatePersonWithNric("s1234567c");
        Person p2 = helper.generatePersonWithNric("s1234567d");

        List<Person> fourPersons = helper.generatePersonList(p1, pTarget1, p2, pTarget2);
        Person expectedPerson = pTarget2;
        String nric = expectedPerson.getNric().getIdentificationNumber();
        helper.addToAddressBook(addressBook, fourPersons);

        assertCommandBehavior("find " + nric,
                Command.getMessageForPersonShownSummary(expectedPerson),
                addressBook,
                false,
                Collections.emptyList());

    }
    */

    @Test
    public void execute_find_differentButValidFile() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithNric("s1234567a");
        Person pTarget2 = helper.generatePersonWithNric("f1234567b");
        String testFile = "testScreen.txt";

        List<Person> persons = helper.generatePersonList(pTarget1, pTarget2);
        String nric = pTarget1.getNric().getIdentificationNumber();
        helper.addToAddressBook(addressBook, persons);

        FindCommand findCommand = new FindCommand(nric);
        findCommand.setFile(testFile);
        findCommand.setAddressBook(addressBook);

        CommandResult r = findCommand.execute();

        assertEquals(testFile, findCommand.getDbName());
        assertEquals(Command.getMessageForPersonShownSummary(pTarget1), r.feedbackToUser);
    }

    @Test
    public void execute_find_invalidFileName() throws Exception {

        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithNric("s1234567a");
        String testFile = "invalidfile.txt";

        List<Person> persons = helper.generatePersonList(pTarget1);
        String nric = pTarget1.getNric().getIdentificationNumber();
        helper.addToAddressBook(addressBook, persons);

        FindCommand findCommand = new FindCommand(nric);
        findCommand.setFile(testFile);
        findCommand.setAddressBook(addressBook);

        CommandResult r = findCommand.execute();
        ExpectedException thrown = ExpectedException.none();
        thrown.expect(IOException.class);

        assertEquals(testFile, findCommand.getDbName());
        assertEquals(MESSAGE_FILE_NOT_FOUND, r.feedbackToUser);
    }

    @Test
    public void execute_find_nonExistentNric() throws Exception {
        String expected = MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
        assertCommandBehavior("find t4444844z", expected);
    }

    @Test
    public void execute_find_isCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person pTarget1 = helper.generatePersonWithNric("s1234567b");
        Person pTarget2 = helper.generatePersonWithNric("s1234567c");
        Person p1 = helper.generatePersonWithNric("s1234567d");
        Person p2 = helper.generatePersonWithNric("s1234567e");

        List<Person> fourPersons = helper.generatePersonList(p1, pTarget1, p2, pTarget2);
        helper.addToAddressBook(addressBook, fourPersons);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find S1234567G",
                expectedMessage,
                addressBook,
                false,
                Collections.emptyList());

    }

    //@@author muhdharun
    @Test
    public void execute_check_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckCommand.MESSAGE_USAGE);
        assertCommandBehavior("check S1234567A", expectedMessage);
        assertCommandBehavior("check s12345a", expectedMessage);
        assertCommandBehavior("check ", expectedMessage);
    }


    @Test
    public void execute_check_validNric() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person toBeAdded = helper.generatePersonWithNric("s1111111a");

        addressBook.addPerson(toBeAdded);

        String nric = toBeAdded.getNric().getIdentificationNumber();
        CheckCommand toCheck = new CheckCommand(nric);
        String invalid = "testScreen.txt";
        toCheck.setFile(invalid);
        assertEquals(invalid, toCheck.getDbName());

        toCheck.setAddressBook(addressBook);
        CommandResult r = toCheck.execute();
        List<String> emptyTimestamps = new ArrayList<>();
        UiFormatter formatter = new UiFormatter();
        String result = formatter.formatForStrings(emptyTimestamps);
        String expectedMessage = result + String.format(MESSAGE_TIMESTAMPS_LISTED_OVERVIEW,
                nric, emptyTimestamps.size());
        assertEquals(expectedMessage, r.feedbackToUser);
    }

    @Test
    public void execute_check_invalidFile() throws Exception {
        String nric = "s1111111a";
        CheckCommand toCheck = new CheckCommand(nric);
        String invalid = "invalidfile.txt";
        toCheck.setFile(invalid);
        assertEquals(invalid, toCheck.getDbName());

        toCheck.setAddressBook(addressBook);
        toCheck.execute();
        ExpectedException thrown = ExpectedException.none();
        thrown.expect(IOException.class);

    }

    @Test
    public void execute_messages_constructor() throws Exception {
        Messages test = new Messages();
        String testMessage = test.MESSAGE_WELCOME_INITIAL;
        assertEquals(testMessage, "Welcome to the Police Records and Intelligent System.");
    }

    @Test
    public void execute_checkPoStatus_correctOutput() throws Exception {
        List<String> allPos = CheckPoStatusCommand.extractEngagementInformation(
                PatrolResourceStatus.getPatrolResourceStatus());
        assertCommandBehavior("checkstatus",
                                Command.getMessage(allPos));
    }

    @Test
    public void execute_updateStatus_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UpdateStatusCommand.MESSAGE_USAGE);
        assertCommandBehavior("updatestatus 111", expectedMessage);
        assertCommandBehavior("updatestatus posq", expectedMessage);
        assertCommandBehavior("updatestatus ", expectedMessage);
    }

    @Test
    public void execute_updateStatus_nonexistingPo() throws Exception {
        String expectedMessage = String.format(Messages.MESSAGE_PO_NOT_FOUND);
        assertCommandBehavior("updatestatus po1234567890123456788", expectedMessage);
    }

    @Test
    public void execute_updateStatus_validPo() throws Exception {
        assertCommandBehavior("updatestatus po1",
                String.format(UpdateStatusCommand.MESSAGE_UPDATE_PO_SUCCESS, "po1"));
    }

    @Test
    public void execute_getMessageForPersonShownSummary_nullInput() throws Exception {
        String expectedMessage = Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
        String actualMessage = Command.getMessageForPersonShownSummary(null);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void execute_patrolResourceUnavailableException_message() throws Exception {
        String po = "po2";
        String expected = String.format("Patrol resource po2 is engaged.");
        assertEquals(expected, new PatrolResourceUnavailableException(po).getMessage());
    }

    @Test
    public void execute_personParametersEquals_equalObjects() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Person test = helper.adam();
        Name name = test.getName();
        Nric nric = test.getNric();
        PostalCode postalCode = test.getPostalCode();
        Status status = test.getStatus();
        Offense offense = test.getWantedFor();

        assertTrue(test.getName().equals(name));
        assertTrue(test.getNric().equals(nric));
        assertTrue(test.getPostalCode().equals(postalCode));
        assertTrue(test.getStatus().equals(status));
        assertTrue(test.getWantedFor().equals(offense));

    }

    //@@author iamputradanish
    @Test
    public void execute_unlockHqp() throws Exception {
        String result = Password.unlockDevice("papa123", 5);
        assertEquals(String.format(Password.MESSAGE_WELCOME , Password.MESSAGE_HQP)
                + "\n" + Password.MESSAGE_ENTER_COMMAND , result);
        Password.lockIsHqp();
    }

    @Test
    public void execute_unlockPo() throws Exception {
        String result = Password.unlockDevice("popo1", 5);
        assertEquals(String.format(Password.MESSAGE_WELCOME , Password.MESSAGE_PO + Password.MESSAGE_ONE)
                + "\n" + Password.MESSAGE_UNAUTHORIZED
                + "\n" + Password.MESSAGE_ENTER_COMMAND , result);
        Password.lockIsPo();
    }

    @Test
    public void execute_wrongPassword_firstTime() throws Exception {
        Password.lockIsPo();
        Password.lockIsHqp();
        String wrongPassword = "thisiswrong";
        int numberOfAttemptsLeft = 5;
        Password.setWrongPasswordCounter(numberOfAttemptsLeft);
        String result = Password.unlockDevice(wrongPassword, numberOfAttemptsLeft);
        assertEquals(Password.MESSAGE_INCORRECT_PASSWORD
                + "\n" + String.format(Password.MESSAGE_ATTEMPTS_LEFT, numberOfAttemptsLeft)
                + "\n" + MESSAGE_ENTER_PASSWORD, result);
        Password.setWrongPasswordCounter(5);
    }
    @Test
    public void execute_wrongPassword_fourthTime() throws Exception {
        Password.lockIsPo();
        Password.lockIsHqp();
        String wrongPassword = "thisiswrong";
        int numberOfAttemptsLeft = 1;
        Password.setWrongPasswordCounter(numberOfAttemptsLeft);
        String result = Password.unlockDevice(wrongPassword, numberOfAttemptsLeft);
        assertEquals(Password.MESSAGE_INCORRECT_PASSWORD
                + "\n" + String.format(Password.MESSAGE_ATTEMPT_LEFT, numberOfAttemptsLeft)
                + "\n" + Password.MESSAGE_SHUTDOWN_WARNING, result);

    }

    @Test
    public void execute_wrongPassword_lastTime() throws Exception {
        Password.lockIsPo();
        Password.lockIsHqp();
        String wrongPassword = "thisiswrong";
        int numberOfAttemptsLeft = 0;
        Password.setWrongPasswordCounter(numberOfAttemptsLeft);
        String result = Password.unlockDevice(wrongPassword, numberOfAttemptsLeft);
        assertEquals(Password.MESSAGE_SHUTDOWN, result);
    }

    @Test
    public void execute_setWrongPasswordCounter_toPositiveNumber() {
        int randomNumber = ThreadLocalRandom.current().nextInt(0, 6);
        Password.setWrongPasswordCounter(randomNumber);
        assertEquals(randomNumber, Password.getWrongPasswordCounter());
    }

    @Test
    public void execute_setWrongPasswordCounter_toNegativeNumber() {
        int randomNumber = ThreadLocalRandom.current().nextInt(-6, 0);
        Password.setWrongPasswordCounter(randomNumber);
        int result = Password.getWrongPasswordCounter();
        assertEquals(0, result);
    }

    @Test
    public void execute_unlockHqpUser() {
        unlockHqp();
        boolean result = Password.isHqpUser();
        assertTrue(result);
    }

    @Test
    public void execute_unlockPoUser() {
        unlockPo();
        boolean result = Password.isPo();
        assertTrue(result);
    }

    @Test
    public void execute_lockHqpUser() {
        Password.lockIsHqp();
        boolean result = Password.isHqpUser();
        assertFalse(result);
    }

    @Test
    public void execute_lockPoUser() {
        Password.lockIsPo();
        boolean result = Password.isPo();
        assertFalse(result);
    }

    @Test
    public void execute_matchPassword_wrongPassword() {
        boolean result = Password.correctPassword("-795402416" , 1092381);
        assertFalse(result);
    }

    @Test
    public void execute_matchPassword_correctPassword() {
        boolean result = Password.correctPassword("-795402416" , -795402416);
        assertTrue(result);
    }


    @Test
    public void execute_wrongHqp() {
        boolean result = correctHqp("hqp", "-795402416" , 123);
        assertFalse(result);
    }

    @Test
    public void execute_wrongPo1() {
        boolean result = correctPO1("po1", "-795402416" , 123);
        assertFalse(result);
    }
    @Test
    public void execute_wrongPo2() {
        boolean result = correctPO2("po2", "-795402416" , 123);
        assertFalse(result);
    }

    @Test
    public void execute_wrongPo3() {
        boolean result = correctPO3("po3", "-795402416" , 123);
        assertFalse(result);
    }
    @Test
    public void execute_wrongPo4() {
        boolean result = correctPO4("po4", "-795402416" , 123);
        assertFalse(result);
    }

    @Test
    public void execute_wrongPo5() {
        boolean result = correctPO5("po5", "-795402416" , 123);
        assertFalse(result);
    }

    @Test
    public void execute_correctHqp() {
        boolean result = correctHqp("hqp", "-795402416" , -795402416);
        assertTrue(result);
    }

    @Test
    public void execute_correctPo1() {
        boolean result = correctPO1("po1", "-795402416" , -795402416);
        assertTrue(result);
    }
    @Test
    public void execute_correctPo2() {
        boolean result = correctPO2("po2", "-795402416" , -795402416);
        assertTrue(result);
    }

    @Test
    public void execute_correctPo3() {
        boolean result = correctPO3("po3", "-795402416" , -795402416);
        assertTrue(result);
    }
    @Test
    public void execute_correctPo4() {
        boolean result = correctPO4("po4", "-795402416" , -795402416);
        assertTrue(result);
    }

    @Test
    public void execute_correctPo5() {
        boolean result = correctPO5("po5", "-795402416" , -795402416);
        assertTrue(result);
    }

    @Test
    public void execute_prepareUpdatePassword() {
        String result = Password.prepareUpdatePassword();
        assertEquals(Password.MESSAGE_ENTER_PASSWORD_TO_CHANGE, result);
        assertTrue(Password.getIsUpdatingPassword());
        assertEquals(5, Password.getWrongPasswordCounter());
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_updatePassword_wrongPassword() throws Exception {
        unlockHqp();
        Password.prepareUpdatePassword();
        Password.logoutUser();
        String result = Password.updatePassword("thisiswrong", 5);
        assertEquals(Password.MESSAGE_INCORRECT_PASSWORD
                + "\n" + String.format(Password.MESSAGE_ATTEMPTS_LEFT, 5)
                + "\n" + MESSAGE_ENTER_PASSWORD, result);
        Password.lockIsHqp();
    }

    @Test
    public void execute_updatePassword_correctHqpPassword() throws Exception {
        Password password = new Password();
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        String result = password.updatePassword("papa123", 5);
        assertEquals(Password.MESSAGE_ENTER_NEW_PASSWORD + Password.MESSAGE_HQP + ":", result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_passwordValidityChecker_tooShort() throws IOException {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "po1";
        int minNumPassword = 5;
        String result = password.passwordValidityChecker(userInput);
        assertEquals(String.format(Password.MESSAGE_PASSWORD_LENGTH, userInput.length())
                + "\n" + String.format(Password.MESSAGE_PASSWORD_MINIMUM_LENGTH, minNumPassword)
                + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_passwordValidityChecker_missingAlphabet() throws IOException {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "123456";
        String result = password.passwordValidityChecker(userInput);
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "alphabet")
                        + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_passwordValidityChecker_missingNumber() throws IOException {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "popopo";
        String result = password.passwordValidityChecker(userInput);
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "number")
                        + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_passwordValidityChecker_missingNumberAndAlphabet() throws IOException {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "*********";
        String result = password.passwordValidityChecker(userInput);
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "alphabet and at least one number")
                        + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_passwordValidityChecker_alreadyExists() throws IOException {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "papa123";
        String result = password.passwordValidityChecker(userInput);
        assertEquals(Password.MESSAGE_PASSWORD_EXISTS + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
    }

    @Test
    public void execute_reenterPassword() throws Exception {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        String userInput = "mama123";
        password.updatePassword("papa123", 5);
        String result = password.updatePassword(userInput, 5);
        assertEquals(Password.MESSAGE_ENTER_NEW_PASSWORD_AGAIN, result);
        assertTrue(Password.isUpdatePasswordConfirmNow());
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
        Password.notUpdatingFinal();
    }

    @Test
    public void execute_updatePasswordFinal_notSame() throws Exception {
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        Password password = new Password();
        Password.setOtp("mama123");
        String result = password.updatePasswordFinal("thisiswrong");
        assertEquals(Password.MESSAGE_NOT_SAME
                + "\n" + Password.MESSAGE_TRY_AGAIN, result);
        assertFalse(isUpdatePasswordConfirmNow());
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
        Password.notUpdatingFinal();
    }

    @Test
    public void execute_updatePasswordFinal_success() throws Exception {
        Password password = new Password();
        Password.unlockHqp();
        Password.prepareUpdatePassword();
        password.updatePassword("papa123", 5);
        String userInput = "mama123";
        Password.setOtp(userInput);
        String result = password.updatePasswordFinal(userInput);
        assertFalse(isUpdatePasswordConfirmNow());
        assertFalse(getIsUpdatingPassword());
        assertEquals(String.format(Password.MESSAGE_UPDATED_PASSWORD, MESSAGE_HQP)
                + "\n" + MESSAGE_ENTER_COMMAND, result);
        password.updatePassword("mama123", 5);
        userInput = "papa123";
        Password.setOtp(userInput);
        password.updatePasswordFinal(userInput);
        Password.lockIsHqp();
        Password.unprepareUpdatePassword();
        Password.notUpdatingFinal();
    }

    @Test
    public void execute_getFullId() {
        String result = getFullId(PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID);
        assertEquals(Password.MESSAGE_HQP, result);
        result = getFullId(PatrolResourceStatus.POLICE_OFFICER_1_ID);
        assertEquals(Password.MESSAGE_PO + Password.MESSAGE_ONE, result);
        result = getFullId(PatrolResourceStatus.POLICE_OFFICER_2_ID);
        assertEquals(Password.MESSAGE_PO + Password.MESSAGE_TWO, result);
        result = getFullId(PatrolResourceStatus.POLICE_OFFICER_3_ID);
        assertEquals(Password.MESSAGE_PO + Password.MESSAGE_THREE, result);
        result = getFullId(PatrolResourceStatus.POLICE_OFFICER_4_ID);
        assertEquals(Password.MESSAGE_PO + Password.MESSAGE_FOUR, result);
        result = getFullId(PatrolResourceStatus.POLICE_OFFICER_5_ID);
        assertEquals(Password.MESSAGE_PO + Password.MESSAGE_FIVE, result);
    }

    @Test
    public void execute_getFullID_ghost() {
        String result = getFullId("nonsense");
        assertEquals("Ghost", result);
    }

    @Test
    public void execute_passwordExistsChecker_exists() throws IOException {
        Password password = new Password();
        String result = password.passwordExistsChecker("papa123");
        assertEquals(Password.MESSAGE_PASSWORD_EXISTS, result);
    }

    @Test
    public void execute_passwordExistsChecker_valid() throws IOException {
        Password password = new Password();
        String result = password.passwordExistsChecker("police123");
        assertEquals(Password.MESSAGE_VALID, result);
    }

    @Test
    public void execute_alphanumericChecker_missingAlphabet() {
        Password password = new Password();
        String result = password.passwordAlphanumericChecker("123132442");
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "alphabet"), result);
    }

    @Test
    public void execute_alphanumericChecker_missingNumber() {
        Password password = new Password();
        String result = password.passwordAlphanumericChecker("papapaapap");
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "number"), result);
    }

    @Test
    public void execute_alphanumericChecker_notAlphanumeric() {
        Password password = new Password();
        String result = password.passwordAlphanumericChecker("*******");
        assertEquals(String.format(Password.MESSAGE_AT_LEAST_ONE, "alphabet and at least one number"), result);
    }

    @Test
    public void execute_alphanumericChecker_valid() {
        Password password = new Password();
        String result = password.passwordAlphanumericChecker("papa123");
        assertEquals(MESSAGE_VALID, result);
    }

    @Test
    public void execute_lengthChecker_tooShort() {
        Password password = new Password();
        String newEnteredPassword = "";
        int lengthPassword = newEnteredPassword.length();
        int minNumPassword = 5;
        String result = password.passwordLengthChecker(newEnteredPassword);
        assertEquals(String.format(MESSAGE_PASSWORD_LENGTH, lengthPassword)
                + "\n" + String.format(MESSAGE_PASSWORD_MINIMUM_LENGTH, minNumPassword), result);
    }

    @Test
    public void execute_lengthChecker_valid() {
        Password password = new Password();
        String newEnteredPassword = "papa123";
        String result = password.passwordLengthChecker(newEnteredPassword);
        assertEquals(MESSAGE_VALID, result);
    }

    @Test
    public void execute_passwordValidityChecker_tooShortAndMissingAlphabet() throws IOException {
        Password password = new Password();
        String newEnteredPassword = "p";
        String result = password.passwordValidityChecker(newEnteredPassword);
        assertEquals(password.passwordLengthChecker(newEnteredPassword)
                + "\n" + password.passwordAlphanumericChecker(newEnteredPassword)
                + "\n" + MESSAGE_TRY_AGAIN, result);
    }

    @Test
    public void execute_getId_hqp() {
        Password.unlockHqp();
        String result = getId();
        assertEquals(PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID, result);
        Password.lockIsHqp();
    }

    //@@author ongweekeong
    @Test
    public void execute_timeCommand() throws Exception {
        String command = DateTimeCommand.COMMAND_WORD;
        TimeAndDate timeAndDate = new TimeAndDate();
        assertTimeCommandBehavior(command, timeAndDate.outputDatHrs());
    }

    @Test
    public void execute_missingInboxFile() {
        String result = "";
        try {
            NotificationReader testReader = new NotificationReader("Nonsense");
            testReader.readFromFile();
        } catch (IOException e) {
            result = MESSAGE_INBOX_FILE_NOT_FOUND;
        }
        assertEquals(MESSAGE_INBOX_FILE_NOT_FOUND, result);
    }

    @Test
    public void execute_inbox_noUnreadMessages() throws Exception {
        Password.lockIsPo();
        Password.lockIsPo();
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        String inputCommand = InboxCommand.COMMAND_WORD;
        final String expected = String.format(InboxCommand.MESSAGE_TOTAL_MESSAGE_NOTIFICATION, 0, 0);
        assertCommandBehavior(inputCommand, expected);
    }

    @Test
    public void execute_inboxSuccessful_readAndUnread() throws Exception {
        Password.lockIsHqp();
        Password.lockIsPo();
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        final String test = "This is the unread test msg";
        Msg testMsg = generateMsgInInbox(test);
        Thread.sleep(50);
        Msg readMsg = generateReadMsgInInbox("This is the read test msg");
        final String inputCommand = InboxCommand.COMMAND_WORD;
        String expected = String.format(InboxCommand.MESSAGE_TOTAL_MESSAGE_NOTIFICATION
                + InboxCommand.concatenateMsg(1, testMsg)
                + InboxCommand.concatenateMsg(2, readMsg), 2, 1);
        assertCommandBehavior(inputCommand, expected, test);
    }


    @Test
    public void execute_readMsg_withoutShowUnread() throws Exception {
        Inbox.setNumUnreadMsgs(-1); // Set numUnreadMsgs to default state before inbox is accessed.
        String inputCommand = ReadCommand.COMMAND_WORD + " 5";
        final String expected = Inbox.INBOX_NOT_READ_YET;
        assertCommandBehavior(inputCommand, expected);
    }

    @Test
    public void execute_checkEmptyInbox_successful() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        CommandResult r = logic.execute(ShowUnreadCommand.COMMAND_WORD);
        assertEquals(Messages.MESSAGE_NO_UNREAD_MSGS, r.feedbackToUser);
    }

    @Test
    public void execute_checkEmptyInbox_afterClearInbox() throws Exception {
        CommandResult r = logic.execute(ClearInboxCommand.COMMAND_WORD);
        final String input = InboxCommand.COMMAND_WORD;
        final String input1 = ShowUnreadCommand.COMMAND_WORD;
        final String expected = String.format(InboxCommand.MESSAGE_TOTAL_MESSAGE_NOTIFICATION, 0, 0);
        final String expected1 = Messages.MESSAGE_NO_UNREAD_MSGS;
        assertCommandBehavior(input, expected);
        assertCommandBehavior(input1, expected1);
    }

    @Test
    public void execute_checkInboxWithAnUnreadMessage_successful() throws Exception {
        Password.lockIsHqp();
        Password.lockIsPo();
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        int messageNum = 1;
        String expectedResult = String.format(Messages.MESSAGE_UNREAD_MSG_NOTIFICATION + '\n', messageNum);
        final String testMessage = "This is a test message.";
        Msg testMsg = generateMsgInInbox(testMessage);
        final String expectedResult1 = String.format(InboxCommand.MESSAGE_TOTAL_MESSAGE_NOTIFICATION, 1, 1);

        assertCommandBehavior(ShowUnreadCommand.COMMAND_WORD, expectedResult, testMsg);
        assertCommandBehavior(InboxCommand.COMMAND_WORD, expectedResult1, testMsg);
    }

    @Test
    public void execute_readMsgWithoutUnreadMsgs_successful() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        Password.lockIsPo();
        Password.lockIsHqp();
        logic.execute(ShowUnreadCommand.COMMAND_WORD);
        String inputCommand = ReadCommand.COMMAND_WORD + " 3";
        final String expected = Inbox.INBOX_NO_UNREAD_MESSAGES;
        assertCommandBehavior(inputCommand, expected);
        logic.execute(InboxCommand.COMMAND_WORD);
        final String expected1 = String.format(Inbox.INBOX_NO_UNREAD_MESSAGES, 0, 0);
        assertCommandBehavior(inputCommand, expected1);
    }

    @Test
    public void execute_readMsgWithOutOfBoundsIndex() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        Password.lockIsHqp();
        Password.lockIsPo(); //Set static boolean flags from other test cases back to original state.
        final int numOfMsgs = 3;
        for (int i = 0; i < numOfMsgs; i++) {
            generateMsgInInbox("This is a test message.");
            Thread.sleep(100);
        }
        CommandResult r = logic.execute(ShowUnreadCommand.COMMAND_WORD);
        final String input1 = ReadCommand.COMMAND_WORD + " 0";
        final String expected1 = String.format(Inbox.INDEX_OUT_OF_BOUNDS, numOfMsgs);
        assertCommandBehavior(input1, expected1);
        final String input2 = ReadCommand.COMMAND_WORD + " 4";
        final String expected2 = String.format(Inbox.INDEX_OUT_OF_BOUNDS, numOfMsgs);
        assertCommandBehavior(input2, expected2);
    }

    @Test
    public void execute_readMsg_invalidIndex() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        final int numOfMsgs = 3;
        for (int i = 0; i < numOfMsgs; i++) {
            generateMsgInInbox("This is a test message.");
            Thread.sleep(100);
        }
        final String inputCommand = ReadCommand.COMMAND_WORD + " a";
        final String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ReadCommand.MESSAGE_USAGE);
        assertCommandBehavior(inputCommand, expected);
    }

    @Test
    public void execute_readMsg_veryLargeIndex() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        generateMsgInInbox("This is a test message.");
        CommandResult r = logic.execute(ShowUnreadCommand.COMMAND_WORD);
        final String inputCommand = ReadCommand.COMMAND_WORD + " 2147483648"; //Outside integer max value
        final String expected = ReadCommand.MESSAGE_INPUT_INDEX_TOO_LARGE;
        assertCommandBehavior(inputCommand, expected);
    }

    @Test
    public void execute_readMsg_validIndex() throws Exception {
        NotificationWriter.clearInbox(MessageFilePaths.FILEPATH_DEFAULT);
        int index = 1;
        final int numOfMsgs = 3;
        for (int i = 0; i < numOfMsgs; i++) {
            generateMsgInInbox("This is a test message. " + index++);
            Thread.sleep(100);
        }
        CommandResult r = logic.execute(ShowUnreadCommand.COMMAND_WORD);
        String inputCommand = ReadCommand.COMMAND_WORD + " " + numOfMsgs;
        final String expected = ReadCommand.MESSAGE_UPDATE_SUCCESS;
        assertCommandBehavior(inputCommand, expected);
    }

    @Test
    public void execute_returnMessageFilePaths_successful() {
        String result = MessageFilePaths.getFilePathFromUserId("hqp");
        String expected = MessageFilePaths.FILEPATH_HQP_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("po1");
        expected = MessageFilePaths.FILEPATH_PO1_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("po2");
        expected = MessageFilePaths.FILEPATH_PO2_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("po3");
        expected = MessageFilePaths.FILEPATH_PO3_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("po4");
        expected = MessageFilePaths.FILEPATH_PO4_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("po5");
        expected = MessageFilePaths.FILEPATH_PO5_INBOX;
        assertEquals(expected, result);

        result = MessageFilePaths.getFilePathFromUserId("nonsense");
        expected = MessageFilePaths.FILEPATH_DEFAULT;
        assertEquals(expected, result);
    }

    @Test
    public void execute_msgComparator() throws Exception {
        final String testMsg = "This is a test message.";
        Msg msgHigh = new Msg(Msg.Priority.HIGH, testMsg);
        Msg msgMed = new Msg(Msg.Priority.MED, testMsg);
        Msg msgLow = new Msg(Msg.Priority.LOW, testMsg);
        final int expectedHighToLow = -1;
        assertEquals(expectedHighToLow, msgHigh.compareTo(msgLow));
        final int expectedHighToMed = -1;
        assertEquals(expectedHighToMed, msgHigh.compareTo(msgMed));
        final int expectedMedToLow = -1;
        assertEquals(expectedMedToLow, msgMed.compareTo(msgLow));
        //Unread messages should have higher urgency than even read high priority messages.
        final int expectedUnreadLowToReadHigh = -1;
        msgHigh.setMsgAsRead();
        assertEquals(expectedUnreadLowToReadHigh, msgLow.compareTo(msgHigh));
        // Messages with same read status and priority will be compared by timestamp.
        final int expectedEarlierToLater = -1;
        Thread.sleep(500);
        Msg msgMedLater = new Msg(Msg.Priority.MED, testMsg);
        assertEquals(expectedEarlierToLater, msgMed.compareTo(msgMedLater));
    }

    @Test
    public void execute_clearInboxCommand_successful() throws Exception {
        String expected = ClearInboxCommand.MESSAGE_CLEARINBOX_SUCCESSFUL;
        assertCommandBehavior(ClearInboxCommand.COMMAND_WORD, expected);
    }

    @Test
    public void execute_clearInboxCommand_unsuccessful() throws Exception {
        final String expected = ClearInboxCommand.MESSAGE_CLEARINBOX_UNSUCCESSFUL;
        Command input = new ClearInboxCommand("This file path does not exist");
        CommandResult r = input.execute();
        assertEquals(expected, r.feedbackToUser);
    }

    @Test
    public void execute_clearRecordedMsgWhenLogout() throws Exception {
        Password.lockIsPo();
        Password.lockIsHqp();
        generateMsgInInbox("populate the inbox!");
        logic.execute(ShowUnreadCommand.COMMAND_WORD);
        assertFalse(Inbox.isRecordMsgsEmpty());
        logic.execute(LogoutCommand.COMMAND_WORD);
        assertTrue(Inbox.isRecordMsgsEmpty());
        assertCommandBehavior(ReadCommand.COMMAND_WORD + " 1", Inbox.INBOX_NOT_READ_YET);
    }

    @Test
    public void execute_clearInbox_thenCheckInbox() throws Exception {
        Password.lockIsHqp();
        Password.lockIsPo();
        generateMsgInInbox("populate the inbox!");
        logic.execute(ClearInboxCommand.COMMAND_WORD);
        assertTrue(Inbox.isRecordMsgsEmpty());
        assertCommandBehavior(InboxCommand.COMMAND_WORD,
                String.format(InboxCommand.MESSAGE_TOTAL_MESSAGE_NOTIFICATION, 0, 0));

    }
    //@@author


    //@@author muhdharun -reused
    class TestDataHelper {
        /**
         * A utility class to generate test data.
         */
        Person adam() throws Exception {
            Name name = new Name("Adam Brown");
            Nric nric = new Nric("f1234567j");
            DateOfBirth dateOfBirth = new DateOfBirth("1900");
            PostalCode postalCode = new PostalCode("444444");
            Status status = new Status("xc");
            Offense wantedFor = new Offense();
            Offense tag1 = new Offense("drugs");
            Offense tag2 = new Offense("riot");
            Set<Offense> tags = new HashSet<>(Arrays.asList(tag1, tag2));
            return new Person(name, nric, dateOfBirth, postalCode, status, wantedFor, tags);
        }


        /**
         * Generates a valid person using the given seed.
         * Running this function with the same parameter values guarantees the returned person will have the same state.
         * Each unique seed will generate a unique Person object.
         *
         * @param seed used to generate the person data field values
         *
         */
        Person generatePerson(int seed) throws Exception {
            return new Person(
                    new Name("Person " + seed),
                    new Nric("g999999" + abs(seed) + "t"),
                    new DateOfBirth(Integer.toString(seed + Integer.parseInt("1901"))),
                    new PostalCode("77777" + seed),
                    new Status("xc"),
                    new Offense(),
                    new HashSet<>(Arrays.asList(new Offense("theft" + abs(seed)), new Offense("theft" + abs(seed + 1))))
            );
        }

        /**
         * @return a dummy person with the given parameters
         * @throws Exception
         */
        Person generateDummyPerson() throws Exception {
            return new Person(
                    new Name("Not a human"),
                    new Nric("f0000000z"),
                    new DateOfBirth("1900"),
                    new PostalCode("777777"),
                    new Status("xc"),
                    new Offense(),
                    new HashSet<>(Arrays.asList(new Offense("theft")))
            );
        }
        /** Generates the correct add command based on the person given */
        String generateAddCommand(Person p) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("add");

            cmd.add(p.getName().toString());
            cmd.add("n/" + p.getNric());
            cmd.add("d/" + p.getDateOfBirth().getDob());
            cmd.add("p/" + p.getPostalCode());
            cmd.add("s/" + p.getStatus());
            cmd.add("w/" + p.getWantedFor().getOffense());

            Set<Offense> tags = p.getPastOffenses();
            for (Offense t: tags) {
                cmd.add("o/" + t.getOffense());
            }

            return cmd.toString();
        }
        //@@author
        /**
         * Generates an AddressBook with auto-generated persons.
         * @param isPrivateStatuses flags to indicate if all contact details of respective persons should be set to
         *                          private.
         */
        AddressBook generateAddressBook(Boolean... isPrivateStatuses) throws Exception {
            AddressBook addressBook = new AddressBook();
            addToAddressBook(addressBook, isPrivateStatuses);
            return addressBook;
        }

        /**
         * Generates an AddressBook based on the list of Persons given.
         */
        AddressBook generateAddressBook(List<Person> persons) throws Exception {
            AddressBook addressBook = new AddressBook();
            addToAddressBook(addressBook, persons);
            return addressBook;
        }

        /**
         * Adds auto-generated Person objects to the given AddressBook
         * @param addressBook The AddressBook to which the Persons will be added
         * @param isPrivateStatuses flags to indicate if all contact details of generated persons should be set to
         *                          private.
         */
        void addToAddressBook(AddressBook addressBook, Boolean... isPrivateStatuses) throws Exception {
            addToAddressBook(addressBook, generatePersonList(isPrivateStatuses));
        }

        /**
         * Adds the given list of Persons to the given AddressBook
         */
        void addToAddressBook(AddressBook addressBook, List<Person> personsToAdd) throws Exception {
            for (Person p: personsToAdd) {
                addressBook.addPerson(p);
            }
        }

        /**
         * Creates a list of Persons based on the give Person objects.
         */
        List<Person> generatePersonList(Person... persons) {
            List<Person> personList = new ArrayList<>();
            for (Person p: persons) {
                personList.add(p);
            }
            return personList;
        }

        /**
         * Generates a list of Persons based on the flags.
         * @param isPrivateStatuses flags to indicate if all contact details of respective persons should be set to
         *                          private.
         */
        List<Person> generatePersonList(Boolean... isPrivateStatuses) throws Exception {
            List<Person> persons = new ArrayList<>();
            int i = 1;
            for (Boolean p: isPrivateStatuses) {
                persons.add(generatePerson(i++));
            }
            return persons;
        }

        /**
         * Generates a random Nric
         */
        //@@author muhdharun
        String generateRandomNric() {
            int min = 1111111;
            int max = 9999999;
            Random r = new Random();
            return "s" + Integer.toString(r.nextInt((max - min) + 1) + min) + "a";
        }

        /**
         * Generates a Person object with given nric. Other fields will have some dummy values.
         */

        Person generatePersonWithNric(String nric) throws Exception {
            return new Person(
                    new Name("Bob"),
                    new Nric(nric),
                    new DateOfBirth("2005"),
                    new PostalCode("123456"),
                    new Status("xc"),
                    new Offense(),
                    Collections.singleton(new Offense("riot"))
            );
        }
        //@@author muhdharun -reused
        /**
        * Generates a Person object with given name. Other fields will have some dummy values.
         */
        Person generatePersonWithName(String name) throws Exception {
            String randomNric = generateRandomNric();
            return new Person(
                 new Name(name),
                 new Nric(randomNric),
                 new DateOfBirth("2005"),
                 new PostalCode("123456"),
                 new Status("xc"),
                 new Offense(),
                 Collections.singleton(new Offense("riot"))
             );
        }

        //@@author ongweekeong
        /**
         * Generates a Msg object with given message. Other fields will have some dummy values.
         * @param message
         * @return
         */
        Msg generateUnreadMsgNoLocation(String message, Msg.Priority urgency) {
            return new Msg(urgency, message);
        }

        Msg generateUnreadMsgWithLocation(String message, Msg.Priority urgency, Location location) {
            return new Msg(urgency, message, location);
        }

        Location generateRandomLocation() {
            return new Location(1.294166, 103.770730);
        }

    }

    //@@author ongweekeong

    /**
     * Used when 2 timestamps compared are within the threshold tolerance values.
     * Sets expected timestmap to the value of the actual timestamp.
     * @param message
     * @param msgNum
     * @return
     * @throws Exception
     */
    Timestamp adjustExpectedTimestamp(String message, int msgNum) throws Exception {
        SimpleDateFormat timeFormatted = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss:SSS");
        Date formattedTime = timeFormatted.parse(parseMsgForDateTimeStamp(message, msgNum));
        Timestamp newTime = new Timestamp(formattedTime.getTime());
        return newTime;
    }

    /**
     * Generates a High priority message in the default inbox with read status set as unread
     * for test purposes.
     * @param testMessage
     * @return
     * @throws Exception
     */
    Msg generateMsgInInbox(String testMessage) throws Exception {
        TestDataHelper messageGenerator = new TestDataHelper();
        Msg testMsg = messageGenerator.generateUnreadMsgNoLocation(testMessage, Msg.Priority.HIGH);
        NotificationWriter testWriter = new NotificationWriter(MessageFilePaths.FILEPATH_DEFAULT, true);
        testWriter.writeToFile(testMsg);
        return testMsg;
    }

    /**
     * Generates a high priority message in the default inbox with read status set to read
     * for testing purposes.
     * @param testMessage
     * @return
     * @throws Exception
     */
    Msg generateReadMsgInInbox(String testMessage) throws Exception {
        TestDataHelper messageGenerator = new TestDataHelper();
        Msg testMsg = messageGenerator.generateUnreadMsgNoLocation(testMessage, Msg.Priority.HIGH);
        testMsg.setMsgAsRead();
        NotificationWriter testWriter = new NotificationWriter(MessageFilePaths.FILEPATH_DEFAULT, true);
        testWriter.writeToFile(testMsg);
        return testMsg;
    }

    /**
     * Parses a Msg converted into a string for the timestamp.
     * @param message
     * @return
     */
    String parseMsgForTimestamp(String message) {
        //int limit = 1 + msgNum;
        String[] timestamp = message.split("-", 2);
        String time = timestamp[1].substring(0, 12);
        return time;
    }

    /**
     * Parses a Msg converted into a string for the date-timestamp.
     * @param message
     * @param msgNum
     * @return
     */
    String parseMsgForDateTimeStamp (String message, int msgNum) {
        int limit = msgNum + 1;
        String[] timestamp = message.split("Sent: ", limit);
        String date = timestamp[msgNum].substring(0, 23);
        return date;
    }

    //@@author ShreyasKp

    @Test
    public void execute_addCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "ad",
            "ade John Doe n/s1234567a d/1996 p/510246 s/xc w/none o/theft o/drugs",
            "ade",
            "adds"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_checkCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "chek",
            "chek s1234567a",
            "chick",
            "checks"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                CheckCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_checkPoStatusCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "checkstats",
            "chickstatus",
            "checkstatuts"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                CheckPoStatusCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_clearCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "cler",
            "cleer",
            "clears"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ClearCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_clearInboxCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "clerinbox",
            "cleerinbox",
            "clearinbux",
            "clearsinbox",
            "clearinboox"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ClearInboxCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_datetimeCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "tim",
            "rime",
            "times"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DateTimeCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }


    @Test
    public void execute_deleteCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "delet",
            "delite",
            "deletes",
            "deletes s1234567a"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_dispatchCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "dispach",
            "dispetch",
            "dispetch PO1 gun PO3",
            "disphatch"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DispatchCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }


    @Test
    public void execute_editCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "edt",
            "exit",
            "exit n/s1234567a p/510247 s/wanted w/murder o/gun",
            "edits"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_findCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "fid",
            "bind",
            "bind s1234567a",
            "fhind"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_helpCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "hel",
            "gelp",
            "helpp"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                HelpCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_inboxCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "ibox",
            "inbux",
            "binbox"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                InboxCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_listCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "lit",
            "kist",
            "lists"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ListCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_logoutCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "logot",
            "logour",
            "logoute"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                LogoutCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_readCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "red",
            "red 1",
            "reed",
            "bread"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ReadCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_requestHelpCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "r",
            "rh",
            "rbp",
            "rbp gun"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RequestHelpCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_showUnreadCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "shounread",
            "showunreed",
            "shiwunread",
            "showsunread",
            "showunbread"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ShowUnreadCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_shutdownCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "shutdon",
            "shutdoen",
            "shutdowns"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ShutdownCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_updateStatusCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "updatstatus",
            "updatestats",
            "updateststus",
            "updatestattus",
            "updatesstatus"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                UpdateStatusCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_viewAllCommand_wrongSpellingOfCommandWord() throws Exception {
        final String[] inputs = {
            "vieall",
            "veewall",
            "veewall 1",
            "viewalll"
        };
        String expected = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewAllCommand.MESSAGE_USAGE)).feedbackToUser;
        assertCommandBehaviourAutocorrect(expected, inputs);
    }

    @Test
    public void execute_addCommand_wrongSpellingOfCommandWordInvalidHqp() throws Exception {
        final String[] inputs = {
            "af",
            "adee John Doe n/s1234567a d/1996 p/510246 s/xc w/none o/theft o/drugs",
            "adsdd"
        };
        Password.unlockHqp();
        boolean isHqp = true;
        String expected = AutoCorrect.getInvalidCommandMessage(isHqp);
        assertCommandBehaviourAutocorrectInvalid(expected, inputs);
    }

    @Test
    public void execute_addCommand_wrongSpellingOfCommandWordInvalidPo() throws Exception {
        final String[] inputs = {
            "af",
            "adee",
            "adsdd John Doe n/s1234567a d/1996 p/510246 s/xc w/none o/theft o/drugs"
        };
        boolean isHqp = false;
        String expected = AutoCorrect.getInvalidCommandMessage(isHqp);
        assertCommandBehaviourAutocorrectInvalid(expected, inputs);
    }

    @Test
    public void execute_clearCommand_wrongSpellingOfCommandWordInvalidHqp() throws Exception {
        final String[] inputs = {
            "celer",
            "vleer",
            "cclears"
        };
        Password.unlockHqp();
        boolean isHqp = true;
        String expected = AutoCorrect.getInvalidCommandMessage(isHqp);
        assertCommandBehaviourAutocorrectInvalid(expected, inputs);
    }

    @Test
    public void execute_clearCommand_wrongSpellingOfCommandWordInvalidPo() throws Exception {
        final String[] inputs = {
            "celer",
            "vleer",
            "cclears"
        };
        boolean isHqp = false;
        String expected = AutoCorrect.getInvalidCommandMessage(isHqp);
        assertCommandBehaviourAutocorrectInvalid(expected, inputs);
    }

    @Test
    public void execute_editCommand_invalidNricPrediction() throws Exception {
        //add people into addressbook
        TestDataHelper helper = new TestDataHelper();

        List<Person> lastShownList = generateAddressbookForAutocorrect();

        helper.addToAddressBook(addressBook, lastShownList);

        String nric = lastShownList.get(1).getNric().toString();

        EditCommand editCommand = new EditCommand(nric, "444444", null, null, null);

        String prediction = findPrediction(nric);

        String expected = editCommand.resultEditPrediction(prediction).feedbackToUser;

        assertCommandBehavior("edit n/f1234566a p/444555 s/xc w/theft o/theft",
                expected,
                addressBook,
                false,
                Collections.emptyList());

    }

    @Test
    public void execute_deleteCommand_invalidNricPrediction() throws Exception {
        //add people into addressbook
        TestDataHelper helper = new TestDataHelper();

        List<Person> lastShownList = generateAddressbookForAutocorrect();

        helper.addToAddressBook(addressBook, lastShownList);

        String nric = lastShownList.get(1).getNric().toString();

        Nric nricObject = new Nric(nric);

        DeleteCommand deleteCommand = new DeleteCommand(nricObject);

        String prediction = findPrediction(nric);

        String expected = deleteCommand.resultDeletePrediction(prediction).feedbackToUser;

        assertCommandBehavior("delete f1234566a",
                expected,
                addressBook,
                false,
                Collections.emptyList());

    }

    @Test
    public void execute_checkCommand_invalidNricPrediction() throws Exception {
        //add people into addressbook
        TestDataHelper helper = new TestDataHelper();

        List<Person> lastShownList = generateAddressbookForAutocorrect();

        helper.addToAddressBook(addressBook, lastShownList);

        String nric = lastShownList.get(1).getNric().toString();

        String prediction = Command.findPrediction(nric);

        String expected = Command.invalidCheckCommandResult(prediction);

        assertCommandBehavior("check f1234566a",
                expected,
                addressBook,
                false,
                Collections.emptyList());

    }
}
