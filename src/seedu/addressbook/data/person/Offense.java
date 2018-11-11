package seedu.addressbook.data.person;
//@@author muhdharun

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.inbox.Msg;

/**
 * Represents a person's offense
 */

public class Offense {
    public static final String EXAMPLE = "theft";
    public static final String MESSAGE_OFFENSE_INVALID = "%s is not a valid offense.\n"
            + "Current command was not executed.\n"
            + "Please try again.\n"
            + "Offense must be inside this list:\n";

    static final String NULL_OFFENSE = "none";

    //@@author andyrobert3
    private static HashMap<String, Msg.Priority> offenseList = new HashMap<>();
    static {
        offenseList.put("none", Msg.Priority.LOW);
        offenseList.put("cheating", Msg.Priority.LOW);
        offenseList.put("piracy", Msg.Priority.LOW);
        offenseList.put("dispute", Msg.Priority.LOW);
        offenseList.put("unsound", Msg.Priority.LOW);
        offenseList.put("theft", Msg.Priority.MED);
        offenseList.put("accident", Msg.Priority.MED);
        offenseList.put("abetment", Msg.Priority.MED);
        offenseList.put("fire", Msg.Priority.MED);
        offenseList.put("outrage-of-modesty", Msg.Priority.MED);
        offenseList.put("murder", Msg.Priority.HIGH);
        offenseList.put("riot", Msg.Priority.HIGH);
        offenseList.put("suspect-loose", Msg.Priority.MED);
        offenseList.put("gun", Msg.Priority.HIGH);
        offenseList.put("gunman", Msg.Priority.HIGH);
        offenseList.put("fleeing suspect", Msg.Priority.HIGH);
        offenseList.put("assault", Msg.Priority.HIGH);
        offenseList.put("attempted-suicide", Msg.Priority.HIGH);
        offenseList.put("drugs", Msg.Priority.HIGH);
        offenseList.put("homicide", Msg.Priority.HIGH);
        offenseList.put("hostage", Msg.Priority.HIGH);
        offenseList.put("house-break", Msg.Priority.HIGH);
        offenseList.put("kidnap", Msg.Priority.HIGH);
        offenseList.put("manslaughter", Msg.Priority.HIGH);
        offenseList.put("rape", Msg.Priority.HIGH);
        offenseList.put("robbery", Msg.Priority.HIGH);
        offenseList.put("wanted", Msg.Priority.HIGH);
        offenseList.put("theft1", Msg.Priority.MED);
        offenseList.put("theft2", Msg.Priority.MED);
        offenseList.put("theft3", Msg.Priority.MED);
        offenseList.put("theft4", Msg.Priority.MED);

    }
    //@@author muhdharun
    private final String offense;

    public Offense() {
        this.offense = "none";
    }

    /**
     * Validates given tag name.
     *
     * @throws IllegalValueException if the given tag name string is invalid.
     */
    public Offense(String offense) throws IllegalValueException {
        offense = offense.toLowerCase().trim();

        if (!isValidOffense(offense)) {
            throw new IllegalValueException(String.format(MESSAGE_OFFENSE_INVALID + "\n"
                    + Offense.getListOfValidOffences(), offense));
        }

        this.offense = offense;
    }

    public static String getListOfValidOffences() {
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Msg.Priority> entry : offenseList.entrySet()) {
            if (!entry.getKey().matches(".*\\d+.*")) {
                result.append(entry.getKey()).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    private static boolean isValidOffense(String offense) {
        return offenseList.containsKey(offense.toLowerCase());
    }

    public String getOffense() {
        return offense;
    }

    //@@author andyrobert3
    /**
     * Returns priority for a given offense.
     *
     * @params offense name
     * @return priority
     */
    public static Msg.Priority getPriority(String offense) throws IllegalValueException {
        offense = offense.toLowerCase();
        if (!offenseList.containsKey(offense)) {
            throw new IllegalValueException(String.format(MESSAGE_OFFENSE_INVALID + "\n"
                    + getListOfValidOffences(), offense));
        }

        return offenseList.get(offense);
    }

    //TODO: If not used, delete
    public static int getPriority(Msg.Priority priority) {
        return priority.toInteger();
    }
    //@@author muhdharun
    public static Set<Offense> getOffenseSet(Set<String> offenseStringSet) throws IllegalValueException {
        Set<Offense> offenseSet = new HashSet<>();

        for (String offense: offenseStringSet) {
            offenseSet.add(new Offense(offense));
        }

        return offenseSet;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Offense // instanceof handles nulls
                && this.offense.equals(((Offense) other).offense)); // state check
    }

    @Override
    public int hashCode() {
        return offense.hashCode();
    }

    @Override
    public String toString() {
        return '[' + offense + ']';
    }
}
