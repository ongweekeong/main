package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;

/**
 * Deletes a person identified using the Nric parameter.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    //@@author muhdharun -reused

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Deletes the person by nric.\n\t"
            + "Parameters: Nric\n\t"
            + "Example: " + COMMAND_WORD + " s1234567a";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";

    private Nric toDelete;

    public DeleteCommand(Nric nric) {
        this.toDelete = nric;
    }


    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = (toDelete == null) ? getTargetPerson() : getTargetPerson(toDelete);
            addressBook.removePerson(target);
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, target));
        } catch (PersonNotFoundException pnfe) {
            //@@author ShreyasKp

            String nric = toDelete.toString();
            String prediction = findPrediction(nric);

            return resultDeletePrediction(prediction);
        }
    }

    /**
     * Finds result of invalid NRIC input
     * @param predictedNricInput The prediction found
     * @return The result of command
     */
    private CommandResult resultDeletePrediction(String predictedNricInput) {

        Dictionary dictionary = new Dictionary();
        if (!predictedNricInput.equals("none")) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK
                    + "\n"
                    + String.format(dictionary.getErrorMessage(), predictedNricInput)
                    + "\n\n" + COMMAND_WORD + " " + predictedNricInput);
        } else {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

}
