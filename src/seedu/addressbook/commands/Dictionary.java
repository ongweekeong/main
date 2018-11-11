//@@author ShreyasKp
package seedu.addressbook.commands;

import java.util.ArrayList;
import java.util.Collections;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.storage.StorageFile;



/**
 * Stores all the COMMANDS to be compared with for autocorrect function.
 */
public class Dictionary extends Command {
    private static final ArrayList<String> COMMANDS = new ArrayList<>();

    private ArrayList<String> details = new ArrayList<>();
    private AddressBook addressBook;

    public Dictionary() {
        try {
            StorageFile storage = new StorageFile();
            this.addressBook = storage.load();
        } catch (Exception e) {
            //TODO: Fix empty catch block
        } finally {
            COMMANDS.add(AddCommand.COMMAND_WORD);
            COMMANDS.add(CheckCommand.COMMAND_WORD);
            COMMANDS.add(CheckPOStatusCommand.COMMAND_WORD);
            COMMANDS.add(ClearCommand.COMMAND_WORD);
            COMMANDS.add(DateTimeCommand.COMMAND_WORD);
            COMMANDS.add(DeleteCommand.COMMAND_WORD);
            COMMANDS.add(DispatchCommand.COMMAND_WORD);
            COMMANDS.add(EditCommand.COMMAND_WORD);
            COMMANDS.add(ShutdownCommand.COMMAND_WORD);
            COMMANDS.add(FindCommand.COMMAND_WORD);
            COMMANDS.add(HelpCommand.COMMAND_WORD);
            COMMANDS.add(ShowUnreadCommand.COMMAND_WORD);
            COMMANDS.add(InboxCommand.COMMAND_WORD);
            COMMANDS.add(ClearInboxCommand.COMMAND_WORD);
            COMMANDS.add(ListCommand.COMMAND_WORD);
            COMMANDS.add(LogoutCommand.COMMAND_WORD);
            COMMANDS.add(ReadCommand.COMMAND_WORD);
            COMMANDS.add(RequestHelpCommand.COMMAND_WORD);
            COMMANDS.add(UpdateStatusCommand.COMMAND_WORD);
            //COMMANDS.add(UpdatePasswordCommand.COMMAND_WORD);
            COMMANDS.add(ViewAllCommand.COMMAND_WORD);
        }
    }

    public String getErrorMessage() {
        return "Did you mean to use %s?"
                    + "\n" + "Please try using the correct implementation of the input as shown below-";
    }

    public String getCommandErrorMessage() {
        return "Did you mean to use %s?"
                    + "\n" + "Please try using the correct implementation of the command as shown below-";
    }

    public static ArrayList<String> getCommands() {
        return COMMANDS;
    }

    public ArrayList<String> getDetails() {
        try {
            for (ReadOnlyPerson person : addressBook.getAllPersons().immutableListView()) {
                details.add(person.getNric().getIdentificationNumber());
            }
            return details;
        } catch (NullPointerException e) {
            return new ArrayList<>(new ArrayList<>(Collections.singletonList("Empty")));
        }
    }
}
