package seedu.addressbook.commands;

//@@author muhdharun
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.storage.StorageFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns the list of timestamps for when a person was screened using 'find' command, if person exists in the records
 */

public class CheckCommand extends Command {

    public static final String COMMAND_WORD = "check";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Gets screening history of person with specified NRIC \n\t"
            + "Parameters: NRIC ...\n\t"
            + "Example: " + COMMAND_WORD + " s1234567a";

    private String nricKeyword;
    private String FILE_NOT_FOUND_ERROR = "File not found";
    private String SCREENING_DATABASE = "ScreeningHistory.txt";
    private AddressBook addressBookForTest; //For testing

    public CheckCommand(String nricToFind)
    {
        this.nricKeyword = nricToFind;
    }

    public String getNricKeyword(){
        return nricKeyword;
    }

    public void setFile(String file) {
        this.SCREENING_DATABASE = file;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBookForTest = addressBook;
        try {
            StorageFile storage = new StorageFile();
            this.addressBook = storage.load();
        } catch(Exception e) {
        }
    }

    public String getDbName() {
        return SCREENING_DATABASE;
    }

    @Override
    public CommandResult execute() {
        final List<String> screeningHistory;
        try {
            screeningHistory = getPersonWithNric(nricKeyword);
            return new CommandResult(getMessageForScreeningHistoryShownSummary(screeningHistory,nricKeyword));
        } catch (IOException e) {
            return new CommandResult(FILE_NOT_FOUND_ERROR);
        }

    }

    /**
     *
     * @param nric
     * @return list of timestamps converted to strings
     */

    private List<String> getPersonWithNric(String nric) throws IOException{
        List<String> screeningHistory;
        screeningHistory = addressBook.readDatabase(nric, SCREENING_DATABASE);

        return screeningHistory;
    }
}
