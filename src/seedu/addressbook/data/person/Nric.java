package seedu.addressbook.data.person;
//@@author muhdharun
import seedu.addressbook.data.exception.IllegalValueException;

/**
 * Represents a Person's identification number(Nric or FIN).
 * Guarantees: immutable; is valid as declared in {@link #isValidNric(String)}
 */
public class Nric {
    public static final String EXAMPLE = "s1234567a";
    public static final String MESSAGE_NAME_CONSTRAINTS = "Nric/FIN should start with 's'/'t'/'g'/'f'(lower case)"
            + "and end with a letter and " + "must have 7 digits in between, no spaces";
    private static final String NAME_VALIDATION_REGEX = "[stgf][0-9]{7}[a-z]";

    private final String identificationNumber;

    /**
     * Validates given nric.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public Nric(String nric) throws IllegalValueException {
        nric = nric.trim();
        if (!isValidNric(nric)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.identificationNumber = nric;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    /**
     * Returns true if a given string is a valid Nric.
     */
    public static boolean isValidNric(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return identificationNumber;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Nric // instanceof handles nulls
                && this.identificationNumber.equals(((Nric) other).identificationNumber)); // state check
    }

    @Override
    public int hashCode() {
        return identificationNumber.hashCode();
    }
}
