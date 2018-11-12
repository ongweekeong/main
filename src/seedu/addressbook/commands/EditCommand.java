//@@author andyrobert3
package seedu.addressbook.commands;

import java.util.Set;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.PostalCode;
import seedu.addressbook.data.person.Status;
import seedu.addressbook.data.person.UniquePersonList;

/**
 * Edits existing person in police records.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Edits the person identified by the Nric number.\n\t"
            + "Parameters: n/NRIC [p/POSTALCODE] [s/STATUS] [w/WANTEDFOR] [o/PASTOFFENSES]...\n\t"
            + "At least one optional tag must be filled.\n\t"
            + "Offenses are added not replaced.\n\t"
            + "Example: " + COMMAND_WORD
            + " n/s1234567a p/510247 s/wanted w/murder o/gun";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %s";
    private Nric nric;
    private PostalCode postalCode;
    private Status status;

    private Offense wantedFor;
    private Set<Offense> offenses;

    public EditCommand(String nric,
                       String postalCode,
                       String status,
                       String wantedFor,
                       Set<String> offenses) throws IllegalValueException {

        this.nric = new Nric(nric);

        if (postalCode != null) {
            this.postalCode = new PostalCode(postalCode);
        }

        if (status != null) {
            this.status = new Status(status);
        }

        if (wantedFor != null) {
            this.wantedFor = new Offense(wantedFor);
        }

        if (offenses != null) {
            this.offenses = Offense.getOffenseSet(offenses);
        }

        if (postalCode == null && status == null && wantedFor == null && offenses == null) {
            throw new IllegalValueException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
    }

    public Nric getNric() {
        return nric;
    }

    public PostalCode getPostalCode() {
        return postalCode;
    }

    public Status getStatus() {
        return status;
    }

    public Offense getWantedFor() {
        return wantedFor;
    }
    /**
     * Updates selected person identified by NRIC with entered attributes
     * @throws UniquePersonList.PersonNotFoundException
     */
    private void updatePerson() throws UniquePersonList.PersonNotFoundException {
        for (Person person : addressBook.getAllPersons()) {
            if (person.getNric().equals(this.nric)) {
                if (postalCode != null) {
                    person.setPostalCode(postalCode);
                }
                if (wantedFor != null) {
                    person.setWantedFor(wantedFor);
                }
                if (status != null) {
                    person.setStatus(status);
                }
                if (offenses != null) {
                    person.addPastOffenses(offenses);
                }
                return;
            }
        }

        throw new UniquePersonList.PersonNotFoundException();
    }

    @Override
    public CommandResult execute() {
        try {
            this.updatePerson();
            return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, this.nric));
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            //@@author ShreyasKp

            String nric = getNric().toString();

            String prediction = findPrediction(nric);

            return resultEditPrediction(prediction);
        }
    }

    //@@author ShreyasKp
    /**
     * Finds result of invalid NRIC input
     * @param predictedNricInput The prediction found
     * @return The result of command
     */
    public CommandResult resultEditPrediction(String predictedNricInput) {

        Dictionary dictionary = new Dictionary();
        if (!predictedNricInput.equals("none")) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK
                    + "\n"
                    + String.format(dictionary.getErrorMessage(), predictedNricInput)
                    + "\n\n" + COMMAND_WORD + " n/" + predictedNricInput + " "
                    + "[p/POSTALCODE] [s/STATUS] [w/WANTEDFOR] [o/PASTOFFENSES]...\n");
        } else {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }
}
