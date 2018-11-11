package seedu.addressbook.data.person;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.addressbook.data.exception.IllegalValueException;

/**
 * Represents a Person in the records.
 * Guarantees: details are present and not null, field values are validated.
 */
//@@author muhdharun -reused
public class Person implements ReadOnlyPerson {

    private static String WANTED_FOR_WARNING = "State the offence if person's status is wanted";

    private Name name;
    private Nric nric;
    private DateOfBirth dateOfBirth;
    private PostalCode postalCode;
    private Status status;
    private Offense wantedFor;

    private Set<Offense> pastOffenses = new HashSet<>();

    /**
     * Assumption: Every field must be present and not null.
     */
    public Person(Name name, Nric nric, DateOfBirth dateOfBirth, PostalCode postalCode, Status status ,
                  Offense wantedFor, Set<Offense> PastOffenses) throws IllegalValueException {
        this.name = name;
        this.nric = nric;
        this.dateOfBirth = dateOfBirth;
        this.postalCode = postalCode;
        this.status = status;
        this.wantedFor = wantedFor;
        if ((this.status.getCurrentStatus().equals(Status.WANTED_KEYWORD))
                && ((this.wantedFor.getOffense().equals(Offense.NULL_OFFENSE))
                || this.wantedFor == null)) {
            throw new IllegalValueException(WANTED_FOR_WARNING);
        } else if (!(this.status.getCurrentStatus().equals(Status.WANTED_KEYWORD))) {

        } else if (!(this.status.getCurrentStatus().equals(this.status.WANTED_KEYWORD))) {

            this.wantedFor = new Offense();
        } else {
            this.wantedFor = wantedFor;
        }

        this.pastOffenses.addAll(PastOffenses);
    }

    /**
     * Copy constructor.
     */
    public Person(ReadOnlyPerson source) throws IllegalValueException {
        this(source.getName(), source.getNric(),
                source.getDateOfBirth(), source.getPostalCode(), source.getStatus(),
                source.getWantedFor(), source.getPastOffenses());
    }

    public static String getWantedForWarning() {
        return WANTED_FOR_WARNING;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Nric getNric() {
        return nric;
    }

    @Override
    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public PostalCode getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Offense getWantedFor() {
        return wantedFor;
    }
    public void setWantedFor(Offense wantedFor) {
        this.wantedFor = wantedFor;
    }


    @Override
    public Set<Offense> getPastOffenses() {
        return pastOffenses;
    }

    /**
     * Replaces this person's offenses with the offenses in {@code newPastOffenses}.
     */
    public void addPastOffenses(Set<Offense> newPastOffenses) {
        pastOffenses.addAll(newPastOffenses);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyPerson // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyPerson) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, nric, dateOfBirth, postalCode, status, wantedFor, pastOffenses);
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

}
