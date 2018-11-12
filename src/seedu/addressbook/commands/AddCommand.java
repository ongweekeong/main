package seedu.addressbook.commands;

import java.util.HashSet;
import java.util.Set;

import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.DateOfBirth;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.PostalCode;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.Status;
import seedu.addressbook.data.person.UniquePersonList;

/**
 * Adds a person to the records.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    //@@ author muhdharun -reused
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds a person to the Police Records. "
            + "Parameters: NAME n/Nric d/DATEOFBIRTH p/POSTALCODE s/STATUS w/WANTEDFOR o/PASTOFFENSES...\n\t"
            + "Example: " + COMMAND_WORD
            + " John Doe n/s1234567a d/1996 p/510246 s/xc w/none o/theft o/drugs";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the Police Records";

    private final Person toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(String name,
                      String nric,
                      String dateOfBirth,
                      String postalCode,
                      String status,
                      String wantedFor,
                      Set<String> pastOffenses) throws IllegalValueException {
        final Set<Offense> pastOffenseSet = new HashSet<>();
        for (String offenseName : pastOffenses) {
            pastOffenseSet.add(new Offense(offenseName));
        }
        this.toAdd = new Person(
                new Name(name),
                new Nric(nric),
                new DateOfBirth(dateOfBirth),
                new PostalCode(postalCode),
                new Status(status),
                new Offense(wantedFor),
                pastOffenseSet
        );
    }


    public AddCommand(Person toAdd) {
        this.toAdd = toAdd;
    }

    public ReadOnlyPerson getPerson() {
        return toAdd;
    }

    @Override
    public CommandResult execute() {
        try {
            addressBook.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (UniquePersonList.DuplicateNricException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        }
    }

}
