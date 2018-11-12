package seedu.addressbook.commands;

//@@author muhdharun

import java.io.IOException;
import java.util.List;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.storage.StorageFile;

/**
 * Returns the list of timestamps for when a person was screened using 'find' command, if person exists in the records
 */

public class CheckCommand extends Command {

    public static final String COMMAND_WORD = "check";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Gets screening history of person with specified Nric \n\t"
            + "Parameters: Nric ...\n\t"
            + "Example: " + COMMAND_WORD + " s1234567a";

    private String nricKeyword;
    private String screeningDatabase = "screeningHistory.txt";
    private AddressBook addressBookForTest; //For testing

    public CheckCommand(String nricToFind) {
        this.nricKeyword = nricToFind;
    }

    public String getNricKeyword() {
        return nricKeyword;
    }

    public void setFile(String file) {
        this.screeningDatabase = file;
    }


    public void setAddressBook(AddressBook addressBook) {
        this.addressBookForTest = addressBook;
        try {
            StorageFile storage = new StorageFile();
            this.addressBook = storage.load();
        } catch (Exception e) {
            //Catch block is empty as this method will only be called during testing as a different
            // addressbook could be used
        }
    }

    public String getDbName() {
        return screeningDatabase;
    }

    @Override
    public CommandResult execute() {
        final List<String> screeningHist;
        try {
            screeningHist = getPersonWithNric(nricKeyword);
            return new CommandResult(getMessageForScreeningHistoryShownSummary(screeningHist, nricKeyword));
        } catch (IOException e) {
            String fileNotFoundError = "File not found";
            return new CommandResult(fileNotFoundError);
        }

    }

    /**
     *
     * @param nric NRIC input
     * @return list of timestamps converted to strings
     */

    private List<String> getPersonWithNric(String nric) throws IOException {
        List<String> screeningHistory = null;
        if (this.addressBookForTest != null) {
            for (ReadOnlyPerson person : addressBookForTest.getAllPersons().immutableListView()) {
                if (person.getNric().getIdentificationNumber().equals(nric)) {
                    screeningHistory = addressBook.readDatabase(nric, screeningDatabase);
                    break;
                }
            }
        } else {
            //@@author ShreyasKp
            for (ReadOnlyPerson person : relevantPersons) {
                if (person.getNric().getIdentificationNumber().equals(nric)) {
                    screeningHistory = addressBook.readDatabase(nric, screeningDatabase);
                    break;

                }
            }
        }

        return screeningHistory;
    }
}
