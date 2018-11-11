package seedu.addressbook.commands;

import java.io.IOException;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.storage.StorageFile;

/**
 * Finds a particular person with the specified Nric, used for screening.
 * Letters in Nric must be in lower case.
 */
public class FindCommand extends Command {
    //@@author muhdharun -reused
    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds person with specified Nric \n\t"
            + "Parameters: Nric ...\n\t"
            + "Example: " + COMMAND_WORD + " s1234567a";

    private String nric;
    private String screeningDatabase = "screeningHistory.txt";
    private AddressBook addressBookForTest; //For testing

    public FindCommand(String nricToFind) {
        this.nric = nricToFind;
    }

    public String getNric() {
        return nric;
    }

    public void setFile(String file) {
        this.screeningDatabase = file;
    }

    /**
     *  Used for testing purposes, especially for when testing for wrong file paths
     */
    public void setAddressBook(AddressBook addressBook) {
        this.addressBookForTest = addressBook;
        try {
            StorageFile storage = new StorageFile();
            this.addressBook = storage.load();
        } catch (Exception e) {
            //TODO: Fix empty catch block
        }
    }

    public String getDbName() {
        return screeningDatabase;
    }

    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson personFound = getPersonWithNric();
            return new CommandResult(getMessageForPersonShownSummary(personFound));
        } catch (IOException ioe) {
            String fileNotFoundError = "File not found";
            return new CommandResult(fileNotFoundError);
        }
    }


    /**
     * Retrieve the person in the records whose name contain the specified Nric.
     *
     * @return Persons found, null if no person found
     */
    private ReadOnlyPerson getPersonWithNric() throws IOException {
        ReadOnlyPerson result = null;
        if (this.addressBookForTest != null) {
            for (ReadOnlyPerson person : this.addressBookForTest.getAllPersons().immutableListView()) {
                if (person.getNric().getIdentificationNumber().equals(nric)) {
                    this.addressBookForTest.addPersonToDbAndUpdate(person);
                    this.addressBookForTest.updateDatabase(screeningDatabase);
                    result = person;
                    break;
                }
            }
        } else {
            for (ReadOnlyPerson person : relevantPersons) {
                if (person.getNric().getIdentificationNumber().equals(nric)) {
                    addressBook.addPersonToDbAndUpdate(person);
                    addressBook.updateDatabase(screeningDatabase);
                    result = person;
                    break;
                }
            }
        }
        return result;
    }

}
