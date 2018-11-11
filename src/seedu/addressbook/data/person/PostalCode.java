package seedu.addressbook.data.person;

//@@author muhdharun
import seedu.addressbook.data.exception.IllegalValueException;

/**
 * Represents a Person's Postal Code in the records.
 * Guarantees: Can be edited using the 'edit' command; is valid as declared in {@link #isValidPostalCode(String)}
 */

public class PostalCode {

    public static final String EXAMPLE = "510123";
    public static final String MESSAGE_NAME_CONSTRAINTS = "Postal Code must be 6 digits long";
    private static final String NAME_VALIDATION_REGEX = "[0-9]{6}";

    private final String postalCode;

    /**
     * Validates given Postal Code.
     *
     * @throws IllegalValueException if given Postal Code string is invalid.
     */
    public PostalCode(String pc) throws IllegalValueException {
        pc = pc.trim();
        if (!isValidPostalCode(pc)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.postalCode = pc;
    }

    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns true if a given string is a valid Nric.
     */
    private static boolean isValidPostalCode(String test) {
        return test.matches(NAME_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return postalCode;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PostalCode // instanceof handles nulls
                && this.postalCode.equals(((PostalCode) other).postalCode)); // state check
    }

    @Override
    public int hashCode() {
        return postalCode.hashCode();
    }

}
