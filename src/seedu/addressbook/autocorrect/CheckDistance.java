//@@author ShreyasKp
package seedu.addressbook.autocorrect;

import java.util.ArrayList;

import seedu.addressbook.commands.Dictionary;

/**
 * Checks the number of single character changes required to convert one string to another.
 */
public class CheckDistance {

    private Dictionary dictionary = new Dictionary();

    private ArrayList<String> commandList = Dictionary.getCommands();

    private ArrayList<String> detailsList = dictionary.getDetails();

    private String prediction = "none";

    /**
     * Checks distance for invalid commands
     * @param commandInput The invalid input command
     * @return The prediction if found
     */
    public String checkDistance(String commandInput) {
        int distance;
        int check = 0;
        for (String command : commandList) {
            distance = EditDistance.computeDistance(commandInput, command);
            if (distance == 1) {
                prediction = command;
                check = 1;
                break;
            }
        }
        if (check == 0) {
            prediction = "none";
        }
        return prediction;
    }

    /**
     * Checks distance for invalid NRICs and finds prediction
     * @param input The invalid input NRIC
     * @return The prediction if found
     */
    public String checkInputDistance(String input) {
        int distance;
        int check = 0;
        for (String nric : detailsList) {
            distance = EditDistance.computeDistance(input, nric);
            if (distance == 1 || distance == 2) {
                prediction = nric;
                check = 1;
                break;
            }
        }
        if (check == 0) {
            prediction = "none";
        }
        return prediction;
    }

    /**
     * Checks the distance for invalid commands
     * @param commandInput The invalid input command
     * @return True if the command is valid
     */
    public Integer checkCommandDistance(String commandInput) {
        int distance;
        int check = 0;
        for (String command : commandList) {
            distance = EditDistance.computeDistance(commandInput, command);
            if (distance == 0) {
                check = 1;
                break;
            }
        }
        return check;
    }
}
