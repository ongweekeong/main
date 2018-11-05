//@@author andyrobert3
package seedu.addressbook.commands;

import seedu.addressbook.autocorrect.CheckDistance;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;

import java.util.Set;

/**
 * Edits existing person in police records.
 */
public class EditCommand extends Command {
    private NRIC nric;
    private PostalCode postalCode;
    private Status status;

    public NRIC getNric() {
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

    private Offense wantedFor;
    private Set<Offense> offenses;

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Edits the person identified by the NRIC number.\n\t"
            + "Parameters: n/NRIC [p/POSTALCODE] [s/STATUS] [w/WANTEDFOR] [o/PASTOFFENSES]...\n\t"
            + "At least one optional tag must be filled.\n\t"
            + "Example: " + COMMAND_WORD
            + " n/s1234567a p/510247 s/wanted w/murder o/gun";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %s";

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

    public EditCommand(String nric,
                       String postalCode,
                       String status,
                       String wantedFor,
                       Set<String> offenses) throws IllegalValueException {

        this.nric = new NRIC(nric);

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
    }

    @Override
    public CommandResult execute() {
        try {
            this.updatePerson();
            return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, this.nric));
        } catch(UniquePersonList.PersonNotFoundException pnfe) {
            //@@author ShreyasKp
            CheckDistance checker = new CheckDistance();

            String nric = getNric().toString();
            String prediction = checker.checkInputDistance(nric);

            if(!prediction.equals("none")) {
                return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK
                        + "\n"
                        + "Did you mean to use "
                        + prediction);
            } else {
                return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK );
            }
        }

    }
}
