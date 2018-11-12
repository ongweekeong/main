package seedu.addressbook.commands;

import static seedu.addressbook.ui.Gui.DISPLAYED_INDEX_OFFSET;

import java.util.List;

import seedu.addressbook.autocorrect.CheckDistance;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;
import seedu.addressbook.ui.UiFormatter;

/**
 * Represents an executable command.
 */
public abstract class Command {
    protected AddressBook addressBook;
    protected List<? extends ReadOnlyPerson> relevantPersons;
    private int targetIndex = -1;

    /**
     * @param targetIndex last visible listing index of the target person
     */
    public Command(int targetIndex) {
        this.setTargetIndex(targetIndex);
    }

    protected Command() {
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of persons.
     *
     * @param personsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForPersonListShownSummary(List<? extends ReadOnlyPerson> personsDisplayed) {

        return "\n\n" + String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, personsDisplayed.size());
    }
    //@@author ShreyasKp
    /**
     * Finds valid NRIC, if it exists
     * @return the prediction found
     */
    public static String findPrediction(String nric) {
        CheckDistance checker = new CheckDistance();

        return checker.checkInputDistance(nric);
    }

    /**
     * Finds result of invalid NRIC input
     * @param predictedNricInput The prediction found
     * @return The result of command
     */
    public static String invalidCheckCommandResult(String predictedNricInput) {

        Dictionary dictionary = new Dictionary();
        if (!predictedNricInput.equals("none")) {
            return (Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK
                    + "\n"
                    + String.format(dictionary.getErrorMessage(), predictedNricInput)
                    + "\n\n" + CheckCommand.COMMAND_WORD + " " + predictedNricInput);
        } else {
            return Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
        }
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of a person.
     *
     * @param personDisplayed used to generate summary
     * @return summary message for person displayed
     */
    //@@author muhdharun
    public static String getMessageForPersonShownSummary(ReadOnlyPerson personDisplayed) {
        if (personDisplayed == null) {
            return Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
        } else {
            String result = personDisplayed.getAsTextShowAllInVerticalMode();
            return result + "\n\n" + String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        }
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of timestamps.
     *
     * @param timestampsDisplayed used to generate summary
     * @param nric to print the specified nric
     * @return summary message for timestamps displayed
     */

    public static String getMessageForScreeningHistoryShownSummary(List<String> timestampsDisplayed, String nric) {
        if (timestampsDisplayed == null) {
            String prediction = findPrediction(nric);

            return invalidCheckCommandResult(prediction);
        } else {

            UiFormatter formatter = new UiFormatter();
            String result = formatter.formatForStrings(timestampsDisplayed);

            return result + String.format(Messages.MESSAGE_TIMESTAMPS_LISTED_OVERVIEW,
                    nric, timestampsDisplayed.size());
        }
    }

    public static String getMessage(List<String> args) {

        UiFormatter formatter = new UiFormatter();
        String result = formatter.formatForStrings(args);

        return result;

    }

    //@@ author
    /**
     * Executes the command and returns the result.
     */
    public CommandResult execute() {
        throw new UnsupportedOperationException("This method should be implement in child classes");
    }

    //Note: it is better to make the execute() method abstract, by replacing the above method with the line below:
    //public abstract CommandResult execute();

    /**
     * Supplies the data the command will operate on.
     */
    public void setData(AddressBook addressBook, List<? extends ReadOnlyPerson> relevantPersons) {
        this.addressBook = addressBook;
        this.relevantPersons = (relevantPersons.isEmpty())
                ? addressBook.getAllPersons().immutableListView() : relevantPersons;
    }

    /**
     * Extracts the the target person in the last shown list from the given arguments.
     *
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected ReadOnlyPerson getTargetPerson() throws IndexOutOfBoundsException {
        return relevantPersons.get(getTargetIndex() - DISPLAYED_INDEX_OFFSET);
    }

    //@@author andyrobert3
    protected ReadOnlyPerson getTargetPerson(Nric nric) throws UniquePersonList.PersonNotFoundException {
        for (ReadOnlyPerson person: relevantPersons) {
            if (person.getNric().getIdentificationNumber().equals(nric.getIdentificationNumber())) {
                return person;
            }
        }
        throw new UniquePersonList.PersonNotFoundException();
    }


    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
}
